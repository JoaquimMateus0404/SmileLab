package com.cleansoft.smilelab.ui.screens.problems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleansoft.smilelab.ui.theme.ModuleProblems

/**
 * Dados de problemas dentários
 */
data class DentalProblem(
    val id: String,
    val emoji: String,
    val name: String,
    val description: String,
    val causes: List<String>,
    val symptoms: List<String>,
    val prevention: List<String>,
    val severity: String
)

val dentalProblems = listOf(
    DentalProblem(
        id = "cavities",
        emoji = "🦷",
        name = "Cárie Dentária",
        description = "Destruição do esmalte e dentina do dente causada por ácidos produzidos por bactérias.",
        causes = listOf(
            "Acúmulo de placa bacteriana",
            "Consumo excessivo de açúcar",
            "Má higiene bucal",
            "Boca seca (pouca saliva)"
        ),
        symptoms = listOf(
            "Dor ao comer doces, quentes ou frios",
            "Manchas escuras no dente",
            "Buracos visíveis",
            "Sensibilidade dentária"
        ),
        prevention = listOf(
            "Escovar os dentes 3x ao dia",
            "Usar fio dental diariamente",
            "Limitar açúcar na dieta",
            "Visitar o dentista regularmente",
            "Usar pasta com flúor"
        ),
        severity = "Comum"
    ),
    DentalProblem(
        id = "gingivitis",
        emoji = "🔴",
        name = "Gengivite",
        description = "Inflamação das gengivas, estágio inicial da doença periodontal. É reversível com tratamento adequado.",
        causes = listOf(
            "Acúmulo de placa bacteriana",
            "Má escovação",
            "Não usar fio dental",
            "Tabagismo",
            "Alterações hormonais"
        ),
        symptoms = listOf(
            "Gengivas vermelhas e inchadas",
            "Sangramento ao escovar ou usar fio dental",
            "Mau hálito persistente",
            "Gengivas sensíveis"
        ),
        prevention = listOf(
            "Escovação correta 3x ao dia",
            "Uso diário de fio dental",
            "Limpeza profissional a cada 6 meses",
            "Não fumar"
        ),
        severity = "Moderada"
    ),
    DentalProblem(
        id = "periodontitis",
        emoji = "⚠️",
        name = "Periodontite",
        description = "Forma avançada de doença gengival que afeta os tecidos e osso que sustentam os dentes. Pode causar perda dentária.",
        causes = listOf(
            "Gengivite não tratada",
            "Má higiene bucal prolongada",
            "Tabagismo",
            "Diabetes não controlada",
            "Predisposição genética"
        ),
        symptoms = listOf(
            "Gengivas que se afastam dos dentes",
            "Bolsas profundas entre dentes e gengiva",
            "Dentes móveis ou separados",
            "Pus entre dentes e gengiva",
            "Mau hálito intenso"
        ),
        prevention = listOf(
            "Tratar gengivite imediatamente",
            "Higiene bucal rigorosa",
            "Visitas regulares ao dentista",
            "Controlar diabetes",
            "Parar de fumar"
        ),
        severity = "Grave"
    ),
    DentalProblem(
        id = "sensitivity",
        emoji = "❄️",
        name = "Sensibilidade Dentária",
        description = "Dor aguda e rápida nos dentes ao consumir alimentos quentes, frios, doces ou ácidos.",
        causes = listOf(
            "Esmalte desgastado",
            "Retração gengival",
            "Escovação agressiva",
            "Bruxismo (ranger os dentes)",
            "Branqueamento excessivo"
        ),
        symptoms = listOf(
            "Dor ao comer gelados ou beber café",
            "Desconforto com alimentos ácidos",
            "Dor ao respirar ar frio",
            "Sensibilidade ao escovar"
        ),
        prevention = listOf(
            "Usar escova de cerdas macias",
            "Não escovar com força",
            "Usar pasta para dentes sensíveis",
            "Evitar alimentos muito ácidos",
            "Usar protetor bucal se ranger os dentes"
        ),
        severity = "Comum"
    ),
    DentalProblem(
        id = "halitosis",
        emoji = "💨",
        name = "Halitose (Mau Hálito)",
        description = "Odor desagradável que sai da boca. Pode ter várias causas, desde má higiene até problemas de saúde.",
        causes = listOf(
            "Má higiene bucal",
            "Não limpar a língua",
            "Boca seca",
            "Certos alimentos (alho, cebola)",
            "Tabagismo",
            "Problemas digestivos"
        ),
        symptoms = listOf(
            "Gosto ruim na boca",
            "Reações negativas de outras pessoas",
            "Língua esbranquiçada"
        ),
        prevention = listOf(
            "Escovar dentes e língua regularmente",
            "Usar fio dental diariamente",
            "Beber bastante água",
            "Mascar chiclete sem açúcar",
            "Evitar tabaco",
            "Consultar médico se persistir"
        ),
        severity = "Moderada"
    )
)

/**
 * Tela de Problemas Dentários
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentalProblemsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚠️ Problemas Dentários",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ModuleProblems.copy(alpha = 0.1f)
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
                        containerColor = ModuleProblems.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Conhecer para Prevenir",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A maioria dos problemas dentários pode ser prevenida com boa higiene bucal e visitas regulares ao dentista. Conhecer os sintomas ajuda a identificar problemas cedo!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Disclaimer
            item {
                DisclaimerBanner()
            }

            // Lista de problemas
            items(dentalProblems) { problem ->
                DentalProblemCard(problem = problem)
            }
        }
    }
}

@Composable
fun DisclaimerBanner() {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.MedicalServices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Se você suspeita de algum problema dentário, consulte um dentista. Este conteúdo é apenas educativo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun DentalProblemCard(problem: DentalProblem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = problem.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = problem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    SeverityBadge(severity = problem.severity)
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Expandir"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = problem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            // Conteúdo expandido
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Causas
                ProblemSection(
                    title = "Causas",
                    emoji = "🔍",
                    items = problem.causes
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sintomas
                ProblemSection(
                    title = "Sintomas",
                    emoji = "📋",
                    items = problem.symptoms
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Prevenção
                ProblemSection(
                    title = "Prevenção",
                    emoji = "🛡️",
                    items = problem.prevention,
                    isPositive = true
                )
            }
        }
    }
}

@Composable
fun SeverityBadge(severity: String) {
    val color = when (severity) {
        "Comum" -> ModuleProblems.copy(alpha = 0.7f)
        "Moderada" -> ModuleProblems
        "Grave" -> MaterialTheme.colorScheme.error
        else -> ModuleProblems
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = severity,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ProblemSection(
    title: String,
    emoji: String,
    items: List<String>,
    isPositive: Boolean = false
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$emoji $title",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { item ->
            Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = if (isPositive) "✓ " else "• ",
                    color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isPositive) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

