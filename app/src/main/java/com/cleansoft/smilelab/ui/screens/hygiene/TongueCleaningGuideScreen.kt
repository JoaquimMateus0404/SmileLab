package com.cleansoft.smilelab.ui.screens.hygiene

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleansoft.smilelab.ui.theme.ModuleHygiene

/**
 * Tela do Guia de Limpeza da Língua
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TongueCleaningGuideScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "👅 Limpeza da Língua",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                TongueCleaningHeader()
            }

            // Por que limpar a língua
            item {
                WhyCleanTongueCard()
            }

            // Como limpar
            item {
                HowToCleanCard()
            }

            // Ferramentas
            item {
                TongueCleaningToolsCard()
            }

            // Dicas
            item {
                TongueCleaningTipsCard()
            }
        }
    }
}

@Composable
fun TongueCleaningHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ModuleHygiene.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👅",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Limpeza da Língua",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ModuleHygiene
                )
                Text(
                    text = " 30 segundos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ModuleHygiene
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Filled.Repeat,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ModuleHygiene
                )
                Text(
                    text = " 2x ao dia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ModuleHygiene
                )
            }
        }
    }
}

@Composable
fun WhyCleanTongueCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = null,
                    tint = ModuleHygiene
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Por que limpar a língua?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "A superfície irregular da língua acumula bactérias, células mortas e restos de alimentos. Esta camada esbranquiçada (saburra lingual) é uma das principais causas do mau hálito!",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            val reasons = listOf(
                "Elimina até 90% das bactérias causadoras de mau hálito",
                "Melhora o paladar",
                "Reduz a placa bacteriana na boca",
                "Contribui para a saúde bucal geral"
            )

            reasons.forEach { reason ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✓ ",
                        color = ModuleHygiene,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun HowToCleanCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Como limpar a língua",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            val steps = listOf(
                "Abra bem a boca e estique a língua para fora",
                "Coloque o limpador de língua ou escova no fundo da língua",
                "Arraste suavemente de trás para frente",
                "Repita 3-5 vezes, cobrindo toda a superfície",
                "Enxague o limpador entre cada passada",
                "Enxague a boca com água ao terminar"
            )

            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun TongueCleaningToolsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Build,
                    contentDescription = null,
                    tint = ModuleHygiene
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ferramentas para limpeza",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ToolItem(
                    emoji = "🔧",
                    name = "Limpador de língua",
                    description = "Ferramenta específica, mais eficaz. Pode ser de plástico ou metal.",
                    recommended = true
                )
                ToolItem(
                    emoji = "🪥",
                    name = "Escova de dentes",
                    description = "Use as cerdas ou a parte de trás (se tiver raspador). Menos eficaz que o limpador.",
                    recommended = false
                )
            }
        }
    }
}

@Composable
fun ToolItem(
    emoji: String,
    name: String,
    description: String,
    recommended: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (recommended) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = ModuleHygiene,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Recomendado",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun TongueCleaningTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                    text = "Dicas importantes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val tips = listOf(
                "Seja gentil - a língua é sensível. Pressão excessiva pode causar irritação.",
                "Limpe de manhã e à noite, após escovar os dentes.",
                "Se sentir ânsia de vômito, comece mais na frente da língua e vá recuando gradualmente.",
                "Uma língua saudável tem cor rosa. Se estiver muito branca, amarela ou com manchas, consulte um dentista."
            )

            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "💡 ",
                        fontSize = 14.sp
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

