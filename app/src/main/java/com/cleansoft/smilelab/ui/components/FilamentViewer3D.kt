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
    modelPath: String = "models/scene.gltf",
    onModelLoaded: () -> Unit = {},
    onError: (Exception) -> Unit = {}
) {
    var sceneManager by remember { mutableStateOf<FilamentSceneManager?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

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
                            // Carregar modelo
                            manager.loadModel(
                                path = modelPath,
                                onProgress = { prog ->
                                    progress = prog
                                },
                                onSuccess = {
                                    isLoading = false
                                    onModelLoaded()
                                },
                                onError = { e ->
                                    isLoading = false
                                    hasError = true
                                    onError(e)
                                }
                            )
                        },
                        onError = { e ->
                            isLoading = false
                            hasError = true
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
 */
private fun handleTouch(manager: FilamentSceneManager, event: MotionEvent): Boolean {
    val manipulator = manager.getCameraManipulator() ?: return false

    when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
            manipulator.grabBegin(event.x.toInt(), event.y.toInt(), false)
        }
        MotionEvent.ACTION_MOVE -> {
            manipulator.grabUpdate(event.x.toInt(), event.y.toInt())
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            manipulator.grabEnd()
        }
    }

    return true
}

