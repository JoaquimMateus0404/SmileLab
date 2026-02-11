package com.cleansoft.smilelab.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Animações customizadas para o SmileLab
 */

// Animação de entrada suave
@Composable
fun AnimatedSlideInVertically(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(200)
        ) + fadeOut(animationSpec = tween(200)),
        content = content
    )
}

// Animação de escala com bounce
@Composable
fun AnimatedScaleIn(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.3f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut(targetScale = 0.8f) + fadeOut(),
        content = content
    )
}

// Animação de shimmer/loading
fun shimmerAnimation(): InfiniteRepeatableSpec<Float> {
    return infiniteRepeatable(
        animation = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        repeatMode = RepeatMode.Restart
    )
}

// Transição de conteúdo com fade
@OptIn(ExperimentalAnimationApi::class)
fun <T> fadeTransition(): ContentTransform {
    return fadeIn(animationSpec = tween(220, delayMillis = 90)) with
            fadeOut(animationSpec = tween(90))
}

// Shake animation para erros
fun shakeAnimation(): KeyframesSpec<Float> {
    return keyframes {
        durationMillis = 500
        0f at 0
        -10f at 100
        10f at 200
        -10f at 300
        10f at 400
        0f at 500
    }
}

