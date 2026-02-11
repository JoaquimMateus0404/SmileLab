package com.cleansoft.smilelab.ui.screens.hygiene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
 * Passo do guia de fio dental
 */
data class FlossingStep(
    val number: Int,
    val title: String,
    val description: String
)

val flossingSteps = listOf(
    FlossingStep(
        number = 1,
        title = "Corte o fio dental",
        description = "Corte aproximadamente 45 cm de fio dental. Pode parecer muito, mas você vai precisar de fio limpo para cada espaço entre os dentes."
    ),
    FlossingStep(
        number = 2,
        title = "Enrole nos dedos",
        description = "Enrole a maior parte do fio ao redor do dedo médio de uma mão. Enrole o resto no dedo médio da outra mão - este dedo vai recolher o fio usado."
    ),
    FlossingStep(
        number = 3,
        title = "Segure firmemente",
        description = "Segure o fio entre os polegares e indicadores, deixando cerca de 2-3 cm de fio esticado entre os dedos."
    ),
    FlossingStep(
        number = 4,
        title = "Deslize com cuidado",
        description = "Deslize o fio suavemente entre os dentes usando um movimento de vai-e-vem. Nunca force o fio - pode machucar a gengiva."
    ),
    FlossingStep(
        number = 5,
        title = "Curve em forma de C",
        description = "Quando o fio alcançar a linha da gengiva, curve-o em forma de 'C' contra um dente. Deslize suavemente no espaço entre a gengiva e o dente."
    ),
    FlossingStep(
        number = 6,
        title = "Limpe cada lado",
        description = "Esfregue o fio suavemente para cima e para baixo contra o lado do dente. Repita no dente adjacente, curvando o fio para o outro lado."
    ),
    FlossingStep(
        number = 7,
        title = "Use fio limpo",
        description = "À medida que avança, desenrole fio limpo de um dedo e enrole o fio usado no outro. Use uma porção limpa para cada espaço interdental."
    ),
    FlossingStep(
        number = 8,
        title = "Não esqueça os últimos dentes",
        description = "Limpe também atrás dos últimos dentes de cada lado, tanto em cima quanto embaixo. Muitas pessoas esquecem desta área!"
    )
)

/**
 * Tela do Guia de Fio Dental
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlossingGuideScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🧵 Guia de Fio Dental",
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
            // Header
            item {
                FlossingHeader()
            }

            // Por que usar fio dental
            item {
                WhyFlossCard()
            }

            // Título dos passos
            item {
                Text(
                    text = "Passo a Passo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Passos
            itemsIndexed(flossingSteps) { index, step ->
                FlossingStepCard(
                    step = step,
                    isLast = index == flossingSteps.size - 1
                )
            }

            // Alternativas ao fio dental
            item {
                AlternativesCard()
            }
        }
    }
}

@Composable
fun FlossingHeader() {
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
                text = "🧵",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Uso do Fio Dental",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Filled.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ModuleHygiene
                )
                Text(
                    text = " 2-3 minutos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ModuleHygiene
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Filled.Repeat,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ModuleHygiene
                )
                Text(
                    text = " 1x ao dia (à noite)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ModuleHygiene
                )
            }
        }
    }
}

@Composable
fun WhyFlossCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Por que usar fio dental?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A escova de dentes limpa apenas 60% da superfície dos dentes. O fio dental alcança os 40% restantes - os espaços entre os dentes onde a escova não chega.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            val benefits = listOf(
                "Remove placa bacteriana entre os dentes",
                "Previne cáries interdentais",
                "Previne doenças da gengiva",
                "Reduz o mau hálito"
            )

            benefits.forEach { benefit ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✓ ",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun FlossingStepCard(step: FlossingStep, isLast: Boolean) {
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
                        .height(80.dp)
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
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AlternativesCard() {
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
                    imageVector = Filled.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Alternativas ao Fio Dental",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val alternatives = listOf(
                "Fita dental" to "Mais larga e confortável para gengivas sensíveis",
                "Fio dental com suporte" to "Mais fácil de usar, ideal para iniciantes",
                "Escova interdental" to "Boa para espaços maiores entre dentes",
                "Irrigador oral" to "Usa jato de água, bom complemento"
            )

            alternatives.forEach { (name, desc) ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "• $name",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

