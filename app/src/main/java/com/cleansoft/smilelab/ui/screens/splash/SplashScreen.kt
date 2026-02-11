package com.cleansoft.smilelab.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Splash Screen do SmileLab
 * Exibe logo e animação inicial enquanto verifica o estado do onboarding
 */
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    isOnboardingCompleted: Boolean?
) {
    // Animações sincronizadas
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    var navigationHandled by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        // Animações em paralelo - mais suaves
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = LinearEasing
                )
            )
        }
    }

    // Navega quando o estado do onboarding estiver disponível
    LaunchedEffect(isOnboardingCompleted) {
        if (isOnboardingCompleted != null && !navigationHandled) {
            // Aguarda animações completarem
            delay(1200)
            navigationHandled = true
            if (isOnboardingCompleted) {
                onNavigateToHome()
            } else {
                onNavigateToOnboarding()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Emoji de dente como placeholder para logo
            Text(
                text = "🦷",
                fontSize = 100.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "SmileLab",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Aprenda a cuidar do seu sorriso",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }

        // Texto de disclaimer no fundo
        Text(
            text = "App educativo - Não substitui consulta profissional",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alpha.value)
        )
    }
}

