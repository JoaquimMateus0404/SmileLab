package com.cleansoft.smilelab.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cleansoft.smilelab.data.repository.ProgressStats
import com.cleansoft.smilelab.ui.theme.SmilePrimary

/**
 * Card de progresso do utilizador para exibir na Home
 */
@Composable
fun UserProgressCard(
    progressStats: ProgressStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SmilePrimary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = SmilePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seu Progresso",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${progressStats.completionPercentage}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SmilePrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progresso
            LinearProgressIndicator(
                progress = { progressStats.completionPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = SmilePrimary,
                trackColor = SmilePrimary.copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Estatísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressStat(
                    value = progressStats.totalViewed.toString(),
                    label = "Visualizados"
                )
                ProgressStat(
                    value = progressStats.totalCompleted.toString(),
                    label = "Completados"
                )
            }
        }
    }
}

@Composable
fun ProgressStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = SmilePrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

