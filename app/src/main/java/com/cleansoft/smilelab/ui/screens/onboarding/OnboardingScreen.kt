package com.cleansoft.smilelab.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Dados de cada página do onboarding
 */
data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String
)

/**
 * Páginas do onboarding educativo
 */
val onboardingPages = listOf(
    OnboardingPage(
        emoji = "🦷",
        title = "Bem-vindo ao SmileLab!",
        description = "O seu guia interativo para aprender tudo sobre saúde bucal de forma divertida e didática."
    ),
    OnboardingPage(
        emoji = "📚",
        title = "Aprenda sobre os Dentes",
        description = "Descubra a anatomia dental, os tipos de dentes e suas funções através de visualização 3D interativa."
    ),
    OnboardingPage(
        emoji = "🪥",
        title = "Técnicas de Higiene",
        description = "Aprenda a escovar corretamente, usar fio dental e manter uma higiene bucal impecável."
    ),
    OnboardingPage(
        emoji = "⚠️",
        title = "Previna Problemas",
        description = "Conheça os problemas dentários mais comuns, como cáries e gengivite, e saiba como preveni-los."
    ),
    OnboardingPage(
        emoji = "⏰",
        title = "Crie Bons Hábitos",
        description = "Configure lembretes de escovação e acompanhe sua rotina de cuidados bucais."
    )
)

/**
 * Tela de Onboarding educativo
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Pager com as páginas
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(page = onboardingPages[page])
            }

            // Indicadores e botões
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicadores de página
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    repeat(onboardingPages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Botões de navegação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão Pular (visível apenas se não estiver na última página)
                    AnimatedVisibility(
                        visible = pagerState.currentPage < onboardingPages.size - 1,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TextButton(onClick = onOnboardingComplete) {
                            Text("Pular")
                        }
                    }

                    // Espaçador se o botão pular não estiver visível
                    if (pagerState.currentPage >= onboardingPages.size - 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Botão Próximo ou Começar
                    Button(
                        onClick = {
                            if (pagerState.currentPage < onboardingPages.size - 1) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onOnboardingComplete()
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = if (pagerState.currentPage < onboardingPages.size - 1)
                                "Próximo" else "Começar!",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = page.emoji,
            fontSize = 100.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            lineHeight = 24.sp
        )
    }
}

