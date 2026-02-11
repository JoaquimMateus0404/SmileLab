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

    // Rendering
    private val choreographer = Choreographer.getInstance()
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (isDestroyed) return

            choreographer.postFrameCallback(this)

            if (!uiHelper.isReadyToRender) return

            try {
                synchronized(this@FilamentSceneManager) {
                    val swap = swapChain ?: return
                    val r = renderer ?: return
                    val v = view ?: return

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
            FilamentEngineManager.runOnGLThread {
                try {
                    createFilamentComponents()
                    setupUiHelper()
                    isInitialized = true

                    Log.d(TAG, "✅ Cena inicializada")
                    onSuccess()

                } catch (e: Exception) {
                    Log.e(TAG, "❌ Erro ao criar componentes", e)
                    onError(e)
                }
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

        view?.scene = scene
        view?.camera = camera

        // Configurar câmera
        camera?.setExposure(16f, 1f / 125f, 100f)

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

        // Luz direcional
        val lightEntity = eng.entityManager.create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1f, 1f, 1f)
            .intensity(100000f)
            .direction(0f, -1f, -1f)
            .castShadows(true)
            .build(eng, lightEntity)
        scn.addEntity(lightEntity)

        // Skybox
        val skybox = Skybox.Builder()
            .color(0.9f, 0.9f, 0.95f, 1f)
            .build(eng)
        scn.skybox = skybox
    }

    /**
     * Configura UiHelper
     */
    private fun setupUiHelper() {
        uiHelper.renderCallback = object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                synchronized(this@FilamentSceneManager) {
                    swapChain?.let { engine?.destroySwapChain(it) }
                    swapChain = engine?.createSwapChain(surface)
                    renderer?.let { displayHelper.attach(it, surfaceView.display) }
                }
            }

            override fun onDetachedFromSurface() {
                synchronized(this@FilamentSceneManager) {
                    swapChain?.let {
                        engine?.destroySwapChain(it)
                        swapChain = null
                    }
                }
            }

            override fun onResized(width: Int, height: Int) {
                view?.viewport = Viewport(0, 0, width, height)
                val aspect = width.toFloat() / height.toFloat()
                camera?.setProjection(45.0, aspect.toDouble(), 0.1, 100.0, Camera.Fov.VERTICAL)

                cameraManipulator = Manipulator.Builder()
                    .viewport(width, height)
                    .targetPosition(0f, 0f, 0f)
                    .orbitHomePosition(0f, 0f, 4f)
                    .build(Manipulator.Mode.ORBIT)
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
                        scene?.addEntities(asset.entities)
                        resourceManager.registerAsset(key, asset)

                        // Centralizar modelo
                        centerModel(asset)

                        onProgress(1.0f)
                        onSuccess(asset)

                        Log.d(TAG, "✅ Modelo adicionado à cena: $key")

                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Erro ao adicionar à cena", e)
                        onError(e)
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
        uiHelper.detach()

        // Destruir recursos na GL thread
        FilamentEngineManager.runOnGLThread {
            try {
                // Destruir assets
                modelLoader?.let { loader ->
                    resourceManager.destroyAll(loader)
                    loader.destroy()
                }

                // Destruir componentes Filament
                engine?.let { eng ->
                    synchronized(this) {
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

