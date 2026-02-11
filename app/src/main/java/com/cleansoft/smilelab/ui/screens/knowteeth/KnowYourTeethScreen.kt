package com.cleansoft.smilelab.ui.screens.knowteeth

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleansoft.smilelab.ui.theme.ModuleKnowTeeth

/**
 * Dados sobre tipos de dentes
 */
data class ToothType(
    val name: String,
    val emoji: String,
    val count: String,
    val function: String,
    val description: String
)

val toothTypes = listOf(
    ToothType(
        name = "Incisivos",
        emoji = "🔲",
        count = "8 dentes (4 superiores, 4 inferiores)",
        function = "Cortar alimentos",
        description = "São os dentes da frente, com bordas afiadas e retas. São os primeiros a aparecer nos bebés e essenciais para cortar alimentos como frutas e vegetais."
    ),
    ToothType(
        name = "Caninos",
        emoji = "🔺",
        count = "4 dentes (2 superiores, 2 inferiores)",
        function = "Rasgar alimentos",
        description = "Também chamados de 'presas', têm forma pontiaguda e são os dentes mais fortes. Servem para rasgar alimentos como carne."
    ),
    ToothType(
        name = "Pré-molares",
        emoji = "⬛",
        count = "8 dentes (4 superiores, 4 inferiores)",
        function = "Triturar alimentos",
        description = "Localizam-se entre os caninos e molares. Têm superfície plana com duas cúspides para começar a triturar os alimentos."
    ),
    ToothType(
        name = "Molares",
        emoji = "🟫",
        count = "12 dentes (incluindo sisos)",
        function = "Moer alimentos",
        description = "São os maiores dentes, localizados no fundo da boca. Têm superfície larga com múltiplas cúspides para moer completamente os alimentos."
    )
)

/**
 * Partes do dente
 */
data class ToothPart(
    val name: String,
    val description: String
)

val toothParts = listOf(
    ToothPart("Esmalte", "Camada externa branca e mais dura do corpo humano. Protege o dente."),
    ToothPart("Dentina", "Camada amarelada abaixo do esmalte. Forma a maior parte do dente."),
    ToothPart("Polpa", "Centro do dente com nervos e vasos sanguíneos. Dá sensibilidade."),
    ToothPart("Raiz", "Parte do dente dentro da gengiva e osso. Fixa o dente no lugar."),
    ToothPart("Gengiva", "Tecido rosa que envolve e protege a base dos dentes.")
)

/**
 * Tela "Conhecer os Dentes"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowYourTeethScreen(
    onNavigateBack: () -> Unit,
    onNavigateTo3DViewer: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tipos de Dentes", "Anatomia")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🦷 Conhecer os Dentes",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ModuleKnowTeeth.copy(alpha = 0.1f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Conteúdo
            when (selectedTab) {
                0 -> ToothTypesContent(onNavigateTo3DViewer = onNavigateTo3DViewer)
                1 -> ToothAnatomyContent(onNavigateTo3DViewer = onNavigateTo3DViewer)
            }
        }
    }
}

@Composable
fun ToothTypesContent(onNavigateTo3DViewer: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Card de introdução
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ModuleKnowTeeth.copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Os adultos têm 32 dentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cada tipo de dente tem uma função específica na mastigação. Conhecê-los ajuda a entender a importância de cuidar de todos!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Lista de tipos de dentes
        items(toothTypes) { tooth ->
            ToothTypeCard(tooth = tooth)
        }

        // Botão para ver em 3D
        item {
            Button(
                onClick = onNavigateTo3DViewer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ViewInAr,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Ver em 3D")
            }
        }
    }
}

@Composable
fun ToothTypeCard(tooth: ToothType) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = tooth.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tooth.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Função: ${tooth.function}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Expandir"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = tooth.count,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = ModuleKnowTeeth
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tooth.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ToothAnatomyContent(onNavigateTo3DViewer: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Card de introdução
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ModuleKnowTeeth.copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Anatomia do Dente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cada dente é composto por várias camadas, cada uma com função específica. Conhecer a estrutura ajuda a entender como as cáries e outros problemas se desenvolvem.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Ilustração simplificada
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🦷",
                        fontSize = 80.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Toque em 'Ver em 3D' para explorar a anatomia interativamente",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Lista de partes do dente
        items(toothParts) { part ->
            ToothPartCard(part = part)
        }

        // Botão para ver em 3D
        item {
            Button(
                onClick = onNavigateTo3DViewer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ViewInAr,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Ver Anatomia em 3D")
            }
        }
    }
}

@Composable
fun ToothPartCard(part: ToothPart) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .offset(y = 6.dp)
                    .background(
                        color = ModuleKnowTeeth,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = part.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = part.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

