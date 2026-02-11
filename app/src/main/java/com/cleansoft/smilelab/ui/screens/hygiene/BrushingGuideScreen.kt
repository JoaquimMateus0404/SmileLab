package com.cleansoft.smilelab.ui.screens.hygiene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleansoft.smilelab.ui.theme.ModuleHygiene

/**
 * Passo do guia de escovação
 */
data class BrushingStep(
    val number: Int,
    val title: String,
    val description: String,
    val duration: String,
    val tip: String? = null
)

val brushingSteps = listOf(
    BrushingStep(
        number = 1,
        title = "Prepare a escova",
        description = "Molhe a escova e coloque uma quantidade de pasta do tamanho de uma ervilha (para adultos). Para crianças, use a quantidade do tamanho de um grão de arroz.",
        duration = "10 segundos",
        tip = "Escolha uma escova de cerdas macias para não danificar o esmalte."
    ),
    BrushingStep(
        number = 2,
        title = "Posicione a escova",
        description = "Incline a escova a 45 graus em direção à linha da gengiva. Esta angulação permite limpar tanto os dentes quanto a área próxima à gengiva.",
        duration = "Preparação",
        tip = "Não pressione com força - movimentos suaves são mais eficazes."
    ),
    BrushingStep(
        number = 3,
        title = "Escove as superfícies externas",
        description = "Com movimentos curtos e suaves (vai-e-vem ou circulares), escove as superfícies externas de todos os dentes, começando pelos dentes de trás.",
        duration = "30 segundos",
        tip = "Divida a boca em 4 quadrantes e dedique tempo igual a cada um."
    ),
    BrushingStep(
        number = 4,
        title = "Escove as superfícies internas",
        description = "Repita o mesmo processo nas superfícies internas (lado da língua). Para os dentes da frente, coloque a escova na vertical e faça movimentos de cima para baixo.",
        duration = "30 segundos"
    ),
    BrushingStep(
        number = 5,
        title = "Escove as superfícies de mastigação",
        description = "Posicione a escova horizontalmente sobre as superfícies de mastigação (parte de cima dos dentes) e faça movimentos de vai-e-vem.",
        duration = "30 segundos",
        tip = "Estas superfícies acumulam restos de comida - dê atenção especial!"
    ),
    BrushingStep(
        number = 6,
        title = "Escove a língua",
        description = "Escove suavemente a língua de trás para frente para remover bactérias e manter o hálito fresco.",
        duration = "20 segundos",
        tip = "Você pode usar um limpador de língua para melhores resultados."
    ),
    BrushingStep(
        number = 7,
        title = "Cuspa e não enxague",
        description = "Cuspa o excesso de pasta, mas não enxague a boca com água! Deixar um pouco de pasta nos dentes permite que o flúor continue a proteger.",
        duration = "Fim",
        tip = "Espere pelo menos 30 minutos antes de comer ou beber."
    )
)

/**
 * Tela do Guia de Escovação
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrushingGuideScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🪥 Guia de Escovação",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
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
            // Header
            item {
                BrushingHeader()
            }

            // Passos
            itemsIndexed(brushingSteps) { index, step ->
                BrushingStepCard(
                    step = step,
                    isLast = index == brushingSteps.size - 1
                )
            }

            // Erros comuns
            item {
                CommonMistakesCard()
            }
        }
    }
}

@Composable
fun BrushingHeader() {
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
                text = "🪥",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Escovação Correta",
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
                    text = " Duração: 2-3 minutos",
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
                    text = " 3x ao dia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ModuleHygiene
                )
            }
        }
    }
}

@Composable
fun BrushingStepCard(step: BrushingStep, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ModuleHygiene),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${step.number}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(100.dp)
                        .background(ModuleHygiene.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Card content
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = ModuleHygiene.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = step.duration,
                            style = MaterialTheme.typography.labelSmall,
                            color = ModuleHygiene,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodySmall
                )

                step.tip?.let { tip ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommonMistakesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Erros Comuns a Evitar",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val mistakes = listOf(
                "Escovar com muita força (danifica o esmalte e a gengiva)",
                "Escovar por menos de 2 minutos",
                "Usar escova de cerdas duras",
                "Escovar logo após comer alimentos ácidos",
                "Usar a mesma escova por mais de 3 meses",
                "Esquecer de escovar a língua"
            )

            mistakes.forEach { mistake ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "✗ ",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = mistake,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

