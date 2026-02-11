package com.cleansoft.smilelab.ui.screens.hygiene

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleansoft.smilelab.ui.theme.ModuleHygiene

/**
 * Dados dos guias de higiene
 */
data class HygieneGuide(
    val id: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val duration: String,
    val importance: String
)

val hygieneGuides = listOf(
    HygieneGuide(
        id = "brushing",
        emoji = "🪥",
        title = "Escovação",
        subtitle = "A base da higiene bucal",
        duration = "2-3 minutos, 3x ao dia",
        importance = "Essencial"
    ),
    HygieneGuide(
        id = "flossing",
        emoji = "🧵",
        title = "Fio Dental",
        subtitle = "Limpeza entre os dentes",
        duration = "2-3 minutos, 1x ao dia",
        importance = "Muito importante"
    ),
    HygieneGuide(
        id = "tongue",
        emoji = "👅",
        title = "Limpeza da Língua",
        subtitle = "Combate o mau hálito",
        duration = "30 segundos",
        importance = "Importante"
    )
)

/**
 * Tela principal de Higiene Bucal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OralHygieneScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBrushingGuide: () -> Unit,
    onNavigateToFlossingGuide: () -> Unit,
    onNavigateToTongueCleaning: () -> Unit
) {
    val guideActions = mapOf(
        "brushing" to onNavigateToBrushingGuide,
        "flossing" to onNavigateToFlossingGuide,
        "tongue" to onNavigateToTongueCleaning
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🪥 Higiene Bucal",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ModuleHygiene.copy(alpha = 0.1f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card de introdução
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ModuleHygiene.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Higiene diária completa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Uma rotina de higiene bucal completa envolve escovação, fio dental e limpeza da língua. Aprenda a técnica correta de cada uma!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Título dos guias
            item {
                Text(
                    text = "Guias de Higiene",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            // Lista de guias
            items(hygieneGuides) { guide ->
                HygieneGuideCard(
                    guide = guide,
                    onClick = { guideActions[guide.id]?.invoke() }
                )
            }

            // Dicas rápidas
            item {
                QuickTipsCard()
            }
        }
    }
}

@Composable
fun HygieneGuideCard(
    guide: HygieneGuide,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = guide.emoji,
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guide.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = guide.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = ModuleHygiene
                    )
                    Text(
                        text = " ${guide.duration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ModuleHygiene
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Abrir guia",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun QuickTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dicas Rápidas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val tips = listOf(
                "Escove os dentes pelo menos 3 vezes ao dia",
                "Troque a escova a cada 3 meses",
                "Não enxague com água após escovar (deixe o flúor agir)",
                "Use fio dental antes de dormir",
                "Visite o dentista a cada 6 meses"
            )

            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "✓ ",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

