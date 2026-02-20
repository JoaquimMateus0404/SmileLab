package com.cleansoft.smilelab.ui.components

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.cleansoft.smilelab.filament.FilamentSceneManager

/**
 * Composable para visualização 3D usando Filament
 * Versão robusta com thread dedicada e error handling
 */
@SuppressLint("ClickableViewAccessibility")
@Composable
fun FilamentViewer3D(
    modifier: Modifier = Modifier,
    modelPath: String = "models/maxillary_left_central_incisor.glb",
    onModelLoaded: () -> Unit = {},
    onError: (Exception) -> Unit = {},
    onSceneManagerReady: (FilamentSceneManager) -> Unit = {}
) {
    var sceneManager by remember { mutableStateOf<FilamentSceneManager?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }
    var loadedPath by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            sceneManager?.destroy()
        }
    }

    Box(modifier = modifier) {
        // SurfaceView para rendering
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                SurfaceView(context).apply {
                    val manager = FilamentSceneManager(context, this)
                    sceneManager = manager

                    // Inicializar cena
                    manager.initialize(
                        onSuccess = {
                            manager.loadModel(
                                path = modelPath,
                                onProgress = { prog ->
                                    progress = prog
                                },
                                onSuccess = {
                                    isLoading = false
                                    loadedPath = modelPath
                                    onModelLoaded()
                                    onSceneManagerReady(manager)
                                },
                                onError = { e ->
                                    isLoading = false
                                    onError(e)
                                }
                            )
                        },
                        onError = { e ->
                            isLoading = false
                            onError(e)
                        }
                    )

                    // Setup gestos
                    setOnTouchListener { _, event ->
                        handleTouch(manager, event)
                        true
                    }
                }
            }
        )

        // Quando modelPath muda, troca o modelo sem recriar a cena
        LaunchedEffect(modelPath, sceneManager) {
            val manager = sceneManager ?: return@LaunchedEffect
            if (loadedPath == null || loadedPath == modelPath) return@LaunchedEffect

            isLoading = true
            progress = 0f

            manager.loadModel(
                path = modelPath,
                key = modelPath,
                replaceCurrent = true,
                onProgress = { prog -> progress = prog },
                onSuccess = {
                    loadedPath = modelPath
                    isLoading = false
                    onModelLoaded()
                },
                onError = { e ->
                    isLoading = false
                    onError(e)
                }
            )
        }

        // Indicador de carregamento
        if (isLoading) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * Manipula eventos de toque para controlar câmera
 * Suporta: rotação (1 dedo), pan (2 dedos), zoom (pinch)
 */
private fun handleTouch(manager: FilamentSceneManager, event: MotionEvent): Boolean {
    if (manager.getCameraManipulator() == null) return false

    when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
            // 1 dedo = rotação
            manager.beginCameraGesture(event.x.toInt(), event.y.toInt(), isPan = false)
        }

        MotionEvent.ACTION_POINTER_DOWN -> {
            // 2+ dedos = pan ou zoom
            if (event.pointerCount == 2) {
                val x = (event.getX(0) + event.getX(1)) / 2
                val y = (event.getY(0) + event.getY(1)) / 2
                manipulator.grabBegin(x.toInt(), y.toInt(), true)  // strafe = true para pan

                // Inicializa a distância anterior do pinch para evitar um salto grande no primeiro movimento
                val dxInit = event.getX(0) - event.getX(1)
                val dyInit = event.getY(0) - event.getY(1)
                val initDistance = kotlin.math.sqrt(dxInit * dxInit + dyInit * dyInit)
                manager.setPreviousPinchDistance(initDistance)
            }
        }

        MotionEvent.ACTION_MOVE -> {
            if (event.pointerCount >= 2) {
                // Calcular centro entre os dois dedos
                val x = (event.getX(0) + event.getX(1)) / 2
                val y = (event.getY(0) + event.getY(1)) / 2

                // Calcular distância para zoom (pinch)
                val dx = event.getX(0) - event.getX(1)
                val dy = event.getY(0) - event.getY(1)
                val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                // Usar razão entre distâncias para tornar o zoom independente de dpi
                val previousDistance = manager.getPreviousPinchDistance()
                var zoomDelta: Float? = null
                if (previousDistance > 0) {
                    val delta = distance - previousDistance
                    zoomDelta = delta * 0.05f
                }
                manager.setPreviousPinchDistance(distance)

                // Pan com dois dedos
                manager.updateCameraGesture(x.toInt(), y.toInt(), zoomDelta)
            } else {
                // Rotação com 1 dedo
                manager.updateCameraGesture(event.x.toInt(), event.y.toInt())
            }
        }

        MotionEvent.ACTION_UP,
        MotionEvent.ACTION_POINTER_UP,
        MotionEvent.ACTION_CANCEL -> {
            manager.endCameraGesture()
            manager.setPreviousPinchDistance(0f)
        }
    }

    return true
}
