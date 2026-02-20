package com.cleansoft.smilelab.filament

import android.content.Context
import android.util.Log
import android.view.Choreographer
import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.utils.Manipulator
import kotlinx.coroutines.*

/**
 * Gerenciador de cena Filament com error handling robusto
 * Orquestra Engine, Scene, Camera, Rendering e Assets
 */
class FilamentSceneManager(
    private val context: Context,
    private val surfaceView: SurfaceView
) {
    private val TAG = "FilamentScene"
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Componentes Filament
    private var engine: Engine? = null
    private var renderer: Renderer? = null
    private var scene: Scene? = null
    private var view: View? = null
    private var camera: Camera? = null

    // UI e Display
    private val uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
    private val displayHelper = DisplayHelper(context)

    // Recursos
    private var modelLoader: ModelLoader? = null
    private val resourceManager = ResourceManager()

    // Estado
    private var swapChain: SwapChain? = null
    private var isInitialized = false
    private var isDestroyed = false
    private var cameraManipulator: Manipulator? = null
    private var previousPinchDistance: Float = 0f  // Para controle de zoom
    private var mainLightEntity: Int? = null
    private var fillLightEntity: Int? = null
    private var backLightEntity: Int? = null
    private var indirectLightIntensity: Float = 12000f
    private var skyboxColor: FloatArray = floatArrayOf(0.4f, 0.4f, 0.45f, 1f)
    private var currentModelKey: String? = null

    // Rendering
    private val choreographer = Choreographer.getInstance()
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (isDestroyed) return

            choreographer.postFrameCallback(this)

            if (!uiHelper.isReadyToRender) return

            FilamentEngineManager.runOnGLThread {
                try {
                    synchronized(this@FilamentSceneManager) {
                        val swap = swapChain ?: return@runOnGLThread
                        val r = renderer ?: return@runOnGLThread
                        val v = view ?: return@runOnGLThread
                        val cam = camera ?: return@runOnGLThread

                        // Atualizar câmera com manipulador
                        cameraManipulator?.let { manipulator ->
                            val eyePos = DoubleArray(3)
                            val target = DoubleArray(3)
                            val upward = DoubleArray(3)
                            manipulator.getLookAt(eyePos, target, upward)
                            cam.lookAt(
                                eyePos[0], eyePos[1], eyePos[2],
                                target[0], target[1], target[2],
                                upward[0], upward[1], upward[2]
                            )
                        }

                        if (r.beginFrame(swap, frameTimeNanos)) {
                            r.render(v)
                            r.endFrame()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "⚠️ Erro no frame callback", e)
                }
            }
        }
    }

    /**
     * Inicializa a cena Filament
     */
    fun initialize(onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        if (isInitialized) {
            Log.d(TAG, "✅ Cena já inicializada")
            onSuccess()
            return
        }

        Log.d(TAG, "🚀 Inicializando cena Filament...")

        try {
            // Garantir que Engine está inicializado
            if (!FilamentEngineManager.initialize()) {
                throw Exception("Falha ao inicializar Engine")
            }

            engine = FilamentEngineManager.getEngine()
                ?: throw Exception("Engine não disponível")

            // Criar componentes na GL thread
            val result = runCatching {
                FilamentEngineManager.runOnGLThreadBlocking {
                    createFilamentComponents()
                } ?: throw IllegalStateException("Falha ao criar componentes na GL thread")
            }

            result.onSuccess {
                setupUiHelper()
                isInitialized = true
                Log.d(TAG, "✅ Cena inicializada")
                onSuccess()
            }.onFailure { error ->
                Log.e(TAG, "❌ Erro ao criar componentes", error)
                onError(error as? Exception ?: Exception(error))
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na inicialização", e)
            onError(e)
        }
    }

    /**
     * Cria componentes Filament
     */
    private fun createFilamentComponents() {
        val eng = engine ?: return

        renderer = eng.createRenderer()
        scene = eng.createScene()
        view = eng.createView()
        camera = eng.createCamera(eng.entityManager.create())

        // Configurar opções de renderização do View
        view?.apply {
            // Anti-aliasing
            antiAliasing = View.AntiAliasing.FXAA

            // Post-processing
            isPostProcessingEnabled = true
        }

        view?.scene = scene
        view?.camera = camera

        // Configurar câmera com exposição adequada
        // setExposure(aperture, shutterSpeed, sensitivity)
        camera?.setExposure(
            4.0f,           // aperture: f/4 (abertura média)
            1f / 60f,       // shutterSpeed: 1/60s
            200f            // sensitivity: ISO 200
        )

        // Setup lighting
        setupLighting()

        // Criar e inicializar ModelLoader NA THREAD GL
        val loader = ModelLoader(context)
        loader.initialize(eng)
        modelLoader = loader

        Log.d(TAG, "✅ Componentes criados")
    }

    /**
     * Configura iluminação
     */
    private fun setupLighting() {
        val eng = engine ?: return
        val scn = scene ?: return

        // Luz direcional principal (sol)
        val sunEntity = eng.entityManager.create()
        mainLightEntity = sunEntity
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1f, 0.98f, 0.95f)  // Luz levemente quente
            .intensity(25000f)
            .direction(-0.5f, -1f, -0.8f)  // Ângulo mais natural
            .castShadows(false)  // Desabilitar sombras para melhor performance
            .build(eng, sunEntity)
        scn.addEntity(sunEntity)

        // Luz de preenchimento (fill light) - para suavizar sombras
        val fillEntity = eng.entityManager.create()
        fillLightEntity = fillEntity
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(0.8f, 0.9f, 1.0f)  // Luz azulada
            .intensity(8000f)
            .direction(0.5f, 0.5f, 1f)  // Vindo de outra direção
            .castShadows(false)
            .build(eng, fillEntity)
        scn.addEntity(fillEntity)

        // Luz de fundo (back light)
        val backEntity = eng.entityManager.create()
        backLightEntity = backEntity
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1f, 1f, 1f)
            .intensity(6000f)
            .direction(0f, 1f, 0.5f)  // De baixo para cima
            .castShadows(false)
            .build(eng, backEntity)
        scn.addEntity(backEntity)

        // IndirectLight (iluminação ambiente) - CRÍTICO para materiais PBR
        try {
            val ibl = createIndirectLight(
                eng,
                indirectLightIntensity,
                skyboxColor[0],
                skyboxColor[1],
                skyboxColor[2]
            )
            scn.indirectLight = ibl
            Log.d(TAG, "✅ IndirectLight configurado")
        } catch (e: Exception) {
            Log.e(TAG, "⚠️ Erro ao criar IndirectLight", e)
        }

        // Skybox com cor neutra
        val skybox = Skybox.Builder()
            .color(skyboxColor[0], skyboxColor[1], skyboxColor[2], skyboxColor[3])
            .build(eng)
        scn.skybox = skybox

        Log.d(TAG, "✅ Sistema de iluminação configurado (3 luzes + IBL)")
    }

    /**
     * Configura UiHelper
     */
    private fun setupUiHelper() {
        uiHelper.renderCallback = object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                FilamentEngineManager.runOnGLThread {
                    synchronized(this@FilamentSceneManager) {
                        swapChain?.let { engine?.destroySwapChain(it) }
                        swapChain = engine?.createSwapChain(surface)
                        renderer?.let { displayHelper.attach(it, surfaceView.display) }
                    }
                }
            }

            override fun onDetachedFromSurface() {
                FilamentEngineManager.runOnGLThread {
                    synchronized(this@FilamentSceneManager) {
                        displayHelper.detach()
                        swapChain?.let {
                            engine?.destroySwapChain(it)
                            swapChain = null
                        }
                    }
                }
            }

            override fun onResized(width: Int, height: Int) {
                FilamentEngineManager.runOnGLThread {
                    view?.viewport = Viewport(0, 0, width, height)
                    val aspect = width.toFloat() / height.toFloat()
                    camera?.setProjection(45.0, aspect.toDouble(), 0.1, 100.0, Camera.Fov.VERTICAL)

                    // Configurar manipulador com zoom out inicial
                    cameraManipulator = Manipulator.Builder()
                        .viewport(width, height)
                        .targetPosition(0f, 0f, 0f)  // Centro do modelo
                        .orbitHomePosition(0f, 1f, 8f)  // Aumentado de 4f para 8f (mais longe)
                        .zoomSpeed(0.2f)  // Velocidade de zoom mais responsiva
                        .build(Manipulator.Mode.ORBIT)

                    Log.d(TAG, "✅ Câmera configurada: zoom inicial = 8 unidades")
                }
            }
        }

        uiHelper.attachTo(surfaceView)
        choreographer.postFrameCallback(frameCallback)
    }

    /**
     * Carrega modelo 3D de forma assíncrona
     */
    fun loadModel(
        path: String,
        key: String = path,
        replaceCurrent: Boolean = true,
        onProgress: (Float) -> Unit = {},
        onSuccess: (FilamentAsset) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        if (!isInitialized) {
            onError(Exception("Cena não inicializada"))
            return
        }

        val loader = modelLoader ?: run {
            onError(Exception("ModelLoader não disponível"))
            return
        }

        scope.launch {
            try {
                Log.d(TAG, "📦 Carregando modelo: $path")
                onProgress(0.1f)

                val asset = loader.loadModel(path)

                if (asset == null) {
                    onError(Exception("Falha ao carregar modelo"))
                    return@launch
                }

                onProgress(0.7f)

                // Adicionar à cena na GL thread
                FilamentEngineManager.runOnGLThread {
                    try {
                        if (replaceCurrent) {
                            currentModelKey?.let { previousKey ->
                                removeAssetFromScene(previousKey)
                            }
                        }

                        scene?.addEntities(asset.entities)
                        resourceManager.registerAsset(key, asset)
                        currentModelKey = key

                        // Centralizar modelo
                        centerModel(asset)

                        scope.launch { onProgress(1.0f) }
                        scope.launch { onSuccess(asset) }

                        Log.d(TAG, "✅ Modelo adicionado à cena: $key")

                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Erro ao adicionar à cena", e)
                        scope.launch { onError(e) }
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao carregar modelo", e)
                onError(e)
            }
        }
    }

    /**
     * Centraliza modelo na cena
     */
    private fun centerModel(asset: FilamentAsset) {
        val eng = engine ?: return

        try {
            val boundingBox = asset.boundingBox
            val center = boundingBox.center
            val halfExtent = boundingBox.halfExtent
            val maxExtent = maxOf(halfExtent[0], halfExtent[1], halfExtent[2])

            if (maxExtent > 0) {
                val rootTransform = asset.root
                val tm = eng.transformManager
                val transformInstance = tm.getInstance(rootTransform)

                val scale = 1f / maxExtent
                tm.setTransform(transformInstance, floatArrayOf(
                    scale, 0f, 0f, 0f,
                    0f, scale, 0f, 0f,
                    0f, 0f, scale, 0f,
                    -center[0] * scale, -center[1] * scale, -center[2] * scale, 1f
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "⚠️ Erro ao centralizar modelo", e)
        }
    }

    /**
     * Retorna manipulador de câmera
     */
    fun getCameraManipulator(): Manipulator? = cameraManipulator

    fun beginCameraGesture(x: Int, y: Int, isPan: Boolean) {
        FilamentEngineManager.runOnGLThread {
            cameraManipulator?.grabBegin(x, y, isPan)
        }
    }

    fun updateCameraGesture(x: Int, y: Int, zoomDelta: Float? = null) {
        FilamentEngineManager.runOnGLThread {
            cameraManipulator?.let { manipulator ->
                zoomDelta?.let { manipulator.scroll(0, 0, it) }
                manipulator.grabUpdate(x, y)
            }
        }
    }

    fun endCameraGesture() {
        FilamentEngineManager.runOnGLThread {
            cameraManipulator?.grabEnd()
        }
    }

    /**
     * Reset da câmera para posição inicial
     */
    fun resetCamera() {
        val v = view ?: return
        val viewport = v.viewport

        FilamentEngineManager.runOnGLThread {
            cameraManipulator = Manipulator.Builder()
                .viewport(viewport.width, viewport.height)
                .targetPosition(0f, 0f, 0f)
                .orbitHomePosition(0f, 1f, 8f)
                .zoomSpeed(0.2f)
                 .build(Manipulator.Mode.ORBIT)

            Log.d(TAG, "🔄 Câmera resetada")
        }
    }

    /**
     * Ajusta intensidade da luz principal.
     */
    fun setMainLightIntensity(intensity: Float) {
        val eng = engine ?: return
        val lightEntity = mainLightEntity ?: return

        FilamentEngineManager.runOnGLThread {
            runCatching {
                val instance = eng.lightManager.getInstance(lightEntity)
                eng.lightManager.setIntensity(instance, intensity.coerceIn(1_000f, 120_000f))
            }.onFailure { Log.e(TAG, "⚠️ Erro ao atualizar luz principal", it) }
        }
    }

    /**
     * Ajusta intensidade da iluminação ambiente (IBL).
     */
    fun setIndirectLightIntensity(intensity: Float) {
        val eng = engine ?: return
        val scn = scene ?: return
        indirectLightIntensity = intensity.coerceIn(2_000f, 30_000f)

        FilamentEngineManager.runOnGLThread {
            runCatching {
                scn.indirectLight?.let { eng.destroyIndirectLight(it) }
                scn.indirectLight = createIndirectLight(
                    eng,
                    indirectLightIntensity,
                    skyboxColor[0],
                    skyboxColor[1],
                    skyboxColor[2]
                )
            }.onFailure { Log.e(TAG, "⚠️ Erro ao atualizar IBL", it) }
        }
    }

    /**
     * Ajusta a cor do skybox para criar variações visuais.
     */
    fun setSkyboxColor(r: Float, g: Float, b: Float) {
        val eng = engine ?: return
        val scn = scene ?: return
        skyboxColor = floatArrayOf(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f), 1f)

        FilamentEngineManager.runOnGLThread {
            runCatching {
                scn.skybox?.let { eng.destroySkybox(it) }
                scn.skybox = Skybox.Builder()
                    .color(skyboxColor[0], skyboxColor[1], skyboxColor[2], skyboxColor[3])
                    .build(eng)

                // Mantém IBL em sintonia com o tom ambiente do skybox
                scn.indirectLight?.let { eng.destroyIndirectLight(it) }
                scn.indirectLight = createIndirectLight(
                    eng,
                    indirectLightIntensity,
                    skyboxColor[0],
                    skyboxColor[1],
                    skyboxColor[2]
                )
            }.onFailure { Log.e(TAG, "⚠️ Erro ao atualizar skybox", it) }
        }
    }

    private fun createIndirectLight(
        eng: Engine,
        intensity: Float,
        r: Float,
        g: Float,
        b: Float
    ): IndirectLight {
        // 1 banda = iluminação difusa uniforme em todas as direções, com tom configurável.
        val irradiance = floatArrayOf(r, g, b)
        return IndirectLight.Builder()
            .irradiance(1, irradiance)
            .intensity(intensity)
            .build(eng)
    }

    private fun removeAssetFromScene(key: String) {
        val scn = scene ?: return
        val loader = modelLoader ?: return

        resourceManager.getAsset(key)?.let { asset ->
            scn.removeEntities(asset.entities)
            resourceManager.destroyAsset(key, loader)
            if (currentModelKey == key) currentModelKey = null
        }
    }

    /**
     * Retorna distância anterior do pinch (para zoom)
     */
    fun getPreviousPinchDistance(): Float = previousPinchDistance

    /**
     * Define distância do pinch (para zoom)
     */
    fun setPreviousPinchDistance(distance: Float) {
        previousPinchDistance = distance
    }

    /**
     * Destroi a cena
     */
    fun destroy() {
        if (isDestroyed) return

        Log.d(TAG, "🧹 Destruindo cena...")
        isDestroyed = true

        // Cancelar coroutines
        scope.cancel()

        // Parar rendering
        choreographer.removeFrameCallback(frameCallback)
        displayHelper.detach()
        uiHelper.detach()

        // Destruir recursos na GL thread
        FilamentEngineManager.runOnGLThreadBlocking {
            try {
                // Destruir assets
                modelLoader?.let { loader ->
                    resourceManager.destroyAll(loader)
                    loader.destroy()
                }

                // Destruir componentes Filament
                engine?.let { eng ->
                    synchronized(this) {
                        displayHelper.detach()
                        swapChain?.let { eng.destroySwapChain(it) }
                        scene?.skybox?.let { eng.destroySkybox(it) }

                        renderer?.let { eng.destroyRenderer(it) }
                        view?.let { eng.destroyView(it) }
                        scene?.let { eng.destroyScene(it) }
                        camera?.let {
                            eng.destroyCameraComponent(it.entity)
                            eng.entityManager.destroy(it.entity)
                        }
                    }
                }

                Log.d(TAG, "✅ Cena destruída")

            } catch (e: Exception) {
                Log.e(TAG, "⚠️ Erro ao destruir cena", e)
            }
        }

        // Limpar referências
        engine = null
        renderer = null
        scene = null
        view = null
        camera = null
        modelLoader = null
        swapChain = null
        isInitialized = false
    }

    /**
     * Debug info
     */
    fun getDebugInfo(): String {
        return buildString {
            appendLine("FilamentSceneManager Debug Info:")
            appendLine("  Initialized: $isInitialized")
            appendLine("  Destroyed: $isDestroyed")
            appendLine("  Engine: ${engine?.let { "OK" } ?: "None"}")
            appendLine("  Renderer: ${renderer?.let { "OK" } ?: "None"}")
            appendLine("  Scene: ${scene?.let { "OK" } ?: "None"}")
            appendLine("  Assets: ${resourceManager.getAssetCount()}")
            appendLine(resourceManager.getDebugInfo())
        }
    }
}
