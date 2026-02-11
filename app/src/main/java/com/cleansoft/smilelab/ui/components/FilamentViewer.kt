package com.cleansoft.smilelab.ui.components

import android.content.Context
import android.view.Choreographer
import android.view.SurfaceView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.*
import com.google.android.filament.utils.*
import java.nio.ByteBuffer

// Companion object para carregar bibliotecas nativas do Filament
private object FilamentLoader {
    init {
        try {
            System.loadLibrary("filament-jni")
            System.loadLibrary("filament-utils-jni")
            System.loadLibrary("gltfio-jni")
            android.util.Log.d("FilamentViewer", "✅ Biblioteca filament-jni carregada com sucesso")
        } catch (e: UnsatisfiedLinkError) {
            android.util.Log.e("FilamentViewer", "❌ Erro ao carregar filament-jni", e)
            throw e
        }
    }

    fun ensureLoaded() {
        // Força inicialização do objeto
    }
}

/**
 * Composable para visualização 3D usando Filament
 */
@Composable
fun FilamentViewer(
    modifier: Modifier = Modifier,
    modelPath: String = "models/scene.gltf",
    onError: ((Exception) -> Unit)? = null
) {
    var filamentHelper by remember { mutableStateOf<FilamentHelper?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            filamentHelper?.destroy()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            SurfaceView(context).also { surfaceView ->
                try {
                    // Garantir que bibliotecas nativas estão carregadas
                    FilamentLoader.ensureLoaded()
                    filamentHelper = FilamentHelper(context, surfaceView, modelPath, onError)
                } catch (e: Exception) {
                    android.util.Log.e("FilamentViewer", "Erro ao criar FilamentHelper", e)
                    onError?.invoke(e)
                }
            }
        },
        update = { _ ->
            // Updates se necessário
        }
    )
}

/**
 * Helper class para gerir Filament Engine
 */
class FilamentHelper(
    private val context: Context,
    private val surfaceView: SurfaceView,
    private val modelPath: String,
    private val onError: ((Exception) -> Unit)?
) {
    private lateinit var engine: Engine
    private lateinit var renderer: Renderer
    private lateinit var scene: Scene
    private lateinit var view: View
    private lateinit var camera: Camera
    private lateinit var assetLoader: AssetLoader
    private lateinit var materialProvider: UbershaderProvider
    private lateinit var resourceLoader: ResourceLoader
    private var asset: FilamentAsset? = null

    private val uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
    private val displayHelper = DisplayHelper(context)
    private val choreographer = Choreographer.getInstance()

    private var swapChain: SwapChain? = null
    private var isDestroyed = false

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (isDestroyed) {
                android.util.Log.w("FilamentViewer", "⚠️ Frame callback chamado após destroy")
                return
            }

            choreographer.postFrameCallback(this)

            if (!uiHelper.isReadyToRender) {
                return
            }

            try {
                synchronized(this@FilamentHelper) {
                    val swap = swapChain ?: return

                    if (!::renderer.isInitialized || !::view.isInitialized) {
                        return
                    }

                    if (renderer.beginFrame(swap, frameTimeNanos)) {
                        renderer.render(view)
                        renderer.endFrame()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("FilamentViewer", "⚠️ Erro no frame callback", e)
                // Não re-lançar para evitar crash do app
            }
        }
    }

    private var cameraManipulator: Manipulator? = null

    init {
        try {
            android.util.Log.d("FilamentViewer", "🚀 Inicializando Filament viewer...")
            initFilament()
            android.util.Log.d("FilamentViewer", "✅ Filament inicializado")

            loadModel()
            android.util.Log.d("FilamentViewer", "✅ Modelo processado")

            setupGestureDetector()
            android.util.Log.d("FilamentViewer", "✅ Gestos configurados")

            choreographer.postFrameCallback(frameCallback)
            android.util.Log.d("FilamentViewer", "✅ Frame callback iniciado")

        } catch (e: Exception) {
            android.util.Log.e("FilamentViewer", "❌ Error initializing Filament viewer", e)
            e.printStackTrace()
            onError?.invoke(e)
            // Re-lançar para evitar uso de objeto parcialmente inicializado
            throw e
        }
    }

    private fun initFilament() {
        engine = Engine.create()
        renderer = engine.createRenderer()
        scene = engine.createScene()
        view = engine.createView()
        camera = engine.createCamera(engine.entityManager.create())

        view.scene = scene
        view.camera = camera

        // Configurar câmera
        camera.setExposure(16f, 1f / 125f, 100f)

        // Configurar iluminação básica
        setupLighting()

        // Configurar UiHelper
        uiHelper.renderCallback = object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: android.view.Surface) {
                synchronized(this@FilamentHelper) {
                    swapChain?.let { engine.destroySwapChain(it) }
                    swapChain = engine.createSwapChain(surface)
                    displayHelper.attach(renderer, surfaceView.display)
                }
            }

            override fun onDetachedFromSurface() {
                synchronized(this@FilamentHelper) {
                    swapChain?.let {
                        engine.destroySwapChain(it)
                        swapChain = null
                    }
                }
            }

            override fun onResized(width: Int, height: Int) {
                view.viewport = Viewport(0, 0, width, height)
                val aspect = width.toFloat() / height.toFloat()
                camera.setProjection(45.0, aspect.toDouble(), 0.1, 100.0, Camera.Fov.VERTICAL)

                cameraManipulator = Manipulator.Builder()
                    .viewport(width, height)
                    .targetPosition(0f, 0f, 0f)
                    .orbitHomePosition(0f, 0f, 4f)
                    .build(Manipulator.Mode.ORBIT)
            }
        }

        uiHelper.attachTo(surfaceView)
    }

    private fun setupLighting() {
        // Criar luz direcional
        val lightEntity = EntityManager.get().create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1f, 1f, 1f)
            .intensity(100000f)
            .direction(0f, -1f, -1f)
            .castShadows(true)
            .build(engine, lightEntity)
        scene.addEntity(lightEntity)

        // Criar skybox com cor sólida
        val skybox = Skybox.Builder()
            .color(0.9f, 0.9f, 0.95f, 1f)
            .build(engine)
        scene.skybox = skybox
    }

    private fun loadModel() {
        // Inicializar providers mesmo sem modelo (evita crash)
        materialProvider = UbershaderProvider(engine)
        assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())
        resourceLoader = ResourceLoader(engine)

        try {
            // Verificar se arquivo existe antes de tentar carregar
            val fileList = context.assets.list("models")
            android.util.Log.d("FilamentViewer", "Arquivos em assets/models/: ${fileList?.joinToString()}")

            if (fileList?.contains("scene.gltf") != true) {
                android.util.Log.w("FilamentViewer", "⚠️ Modelo 3D não encontrado em assets/models/scene.gltf")
                android.util.Log.w("FilamentViewer", "O visualizador 3D funcionará sem modelo.")
                return
            }

            val inputStream = context.assets.open(modelPath)
            val bytes = inputStream.readBytes()
            inputStream.close()

            android.util.Log.d("FilamentViewer", "📦 Carregando modelo: $modelPath (${bytes.size} bytes)")

            val buffer = ByteBuffer.allocateDirect(bytes.size)
            buffer.put(bytes)
            buffer.flip()

            asset = assetLoader.createAsset(buffer)
            asset?.let { filamentAsset ->
                android.util.Log.d("FilamentViewer", "✅ Asset criado, carregando recursos...")

                // Carregar recursos de forma síncrona (mais seguro)
                resourceLoader.loadResources(filamentAsset)

                filamentAsset.releaseSourceData()
                scene.addEntities(filamentAsset.entities)

                android.util.Log.d("FilamentViewer", "✅ Modelo 3D carregado com sucesso!")

                // Centralizar modelo
                val boundingBox = filamentAsset.boundingBox
                val center = boundingBox.center
                val halfExtent = boundingBox.halfExtent
                val maxExtent = maxOf(halfExtent[0], halfExtent[1], halfExtent[2])

                if (maxExtent > 0) {
                    val rootTransform = filamentAsset.root
                    val tm = engine.transformManager
                    val transformInstance = tm.getInstance(rootTransform)

                    val scale = 1f / maxExtent
                    tm.setTransform(transformInstance, floatArrayOf(
                        scale, 0f, 0f, 0f,
                        0f, scale, 0f, 0f,
                        0f, 0f, scale, 0f,
                        -center[0] * scale, -center[1] * scale, -center[2] * scale, 1f
                    ))
                }
            } ?: run {
                android.util.Log.e("FilamentViewer", "❌ Failed to create asset from model: $modelPath")
            }
        } catch (e: java.io.FileNotFoundException) {
            android.util.Log.w("FilamentViewer", "⚠️ Model file not found: $modelPath")
            android.util.Log.w("FilamentViewer", "Please add scene.gltf to app/src/main/assets/models/")
        } catch (e: Exception) {
            android.util.Log.e("FilamentViewer", "❌ Error loading 3D model: $modelPath", e)
            e.printStackTrace()
        }
    }

    private fun setupGestureDetector() {
        var previousDistance = 0f

        @Suppress("ClickableViewAccessibility")
        surfaceView.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    cameraManipulator?.grabBegin(event.x.toInt(), event.y.toInt(), false)
                }
                android.view.MotionEvent.ACTION_POINTER_DOWN -> {
                    if (event.pointerCount == 2) {
                        val dx = event.getX(0) - event.getX(1)
                        val dy = event.getY(0) - event.getY(1)
                        previousDistance = kotlin.math.sqrt(dx * dx + dy * dy)
                    }
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1) {
                        cameraManipulator?.grabUpdate(event.x.toInt(), event.y.toInt())
                    } else if (event.pointerCount == 2) {
                        // Pinch to zoom
                        val dx = event.getX(0) - event.getX(1)
                        val dy = event.getY(0) - event.getY(1)
                        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                        val delta = (previousDistance - distance) * 0.01f
                        cameraManipulator?.scroll(event.x.toInt(), event.y.toInt(), delta)
                        previousDistance = distance
                    }
                    updateCameraFromManipulator()
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    cameraManipulator?.grabEnd()
                    v.performClick()
                }
            }
            true
        }
    }

    private fun updateCameraFromManipulator() {
        cameraManipulator?.let { manipulator ->
            val eyePos = FloatArray(3)
            val target = FloatArray(3)
            val upward = FloatArray(3)
            manipulator.getLookAt(eyePos, target, upward)
            camera.lookAt(
                eyePos[0].toDouble(), eyePos[1].toDouble(), eyePos[2].toDouble(),
                target[0].toDouble(), target[1].toDouble(), target[2].toDouble(),
                upward[0].toDouble(), upward[1].toDouble(), upward[2].toDouble()
            )
        }
    }

    fun destroy() {
        try {
            android.util.Log.d("FilamentViewer", "🧹 Destruindo Filament viewer...")

            // Marcar como destruído PRIMEIRO para parar frame callback
            isDestroyed = true

            // Parar frame callback
            choreographer.removeFrameCallback(frameCallback)

            // Detach UI
            uiHelper.detach()

            // Destruir asset e recursos na ordem correta
            asset?.let {
                scene.removeEntities(it.entities)
                assetLoader.destroyAsset(it)
                asset = null
            }

            if (::resourceLoader.isInitialized) {
                resourceLoader.asyncCancelLoad()
                resourceLoader.evictResourceData()
            }

            if (::materialProvider.isInitialized) {
                materialProvider.destroyMaterials()
            }

            // Destruir componentes Filament na ordem inversa de criação
            if (::engine.isInitialized) {
                // Destruir swap chain
                synchronized(this) {
                    swapChain?.let {
                        engine.destroySwapChain(it)
                        swapChain = null
                    }
                }

                // Destruir skybox
                scene.skybox?.let { engine.destroySkybox(it) }

                // Destruir entidades de luz
                val em = engine.entityManager
                scene.entities.forEach { entity ->
                    if (engine.lightManager.hasComponent(entity)) {
                        engine.destroyEntity(entity)
                        em.destroy(entity)
                    }
                }

                // Destruir componentes principais
                engine.destroyCameraComponent(camera.entity)
                em.destroy(camera.entity)

                engine.destroyRenderer(renderer)
                engine.destroyView(view)
                engine.destroyScene(scene)

                // Destruir engine por último
                engine.destroy()
            }

            android.util.Log.d("FilamentViewer", "✅ Filament viewer destruído com sucesso")
        } catch (e: Exception) {
            android.util.Log.e("FilamentViewer", "⚠️ Error destroying Filament viewer", e)
            e.printStackTrace()
        }
    }
}

