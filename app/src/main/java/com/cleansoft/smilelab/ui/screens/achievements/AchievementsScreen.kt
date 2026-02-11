package com.cleansoft.smilelab.ui.screens.achievements

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleansoft.smilelab.data.model.Achievement
import com.cleansoft.smilelab.data.model.Achievements
import com.cleansoft.smilelab.ui.theme.SmilePrimary
import com.cleansoft.smilelab.ui.theme.SmileSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onNavigateBack: () -> Unit
) {
    // TODO: Carregar do repository
    var achievements by remember { mutableStateOf(Achievements.getAll()) }
    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🏆 Conquistas",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header com progresso geral
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                AchievementsHeader(unlockedCount, totalCount)
            }

            // Grid de conquistas
            items(achievements) { achievement ->
                AchievementCard(achievement)
            }
        }
    }
}

@Composable
fun AchievementsHeader(unlockedCount: Int, totalCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SmilePrimary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$unlockedCount / $totalCount",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = SmilePrimary
            )
            Text(
                text = "Conquistas Desbloqueadas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { unlockedCount.toFloat() / totalCount.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = SmilePrimary,
                trackColor = SmilePrimary.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    val scale by animateFloatAsState(
        targetValue = if (achievement.isUnlocked) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {
                // Ícone
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (achievement.isUnlocked) {
                                Brush.linearGradient(
                                    colors = listOf(SmilePrimary, SmileSecondary)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(Color.Gray, Color.DarkGray)
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.icon,
                        fontSize = 32.sp,
                        modifier = Modifier.graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Título
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    color = if (achievement.isUnlocked)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Descrição
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )

                // Progress bar se não desbloqueada
                if (!achievement.isUnlocked && achievement.maxProgress > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { achievement.progressPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = SmilePrimary.copy(alpha = 0.5f),
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "${achievement.progress}/${achievement.maxProgress}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            }

            // Badge de "Desbloqueado"
            if (achievement.isUnlocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(SmilePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

