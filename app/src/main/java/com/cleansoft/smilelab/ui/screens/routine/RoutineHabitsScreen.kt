package com.cleansoft.smilelab.ui.screens.routine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.cleansoft.smilelab.ui.theme.ModuleRoutine

/**
 * Tela de Rotina & Hábitos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineHabitsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReminders: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📅 Rotina & Hábitos",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ModuleRoutine.copy(alpha = 0.1f)
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
                RoutineHeader()
            }

            // Rotina diária ideal
            item {
                DailyRoutineCard()
            }

            // Hábitos saudáveis
            item {
                HealthyHabitsCard()
            }

            // Hábitos a evitar
            item {
                HabitsToAvoidCard()
            }

            // Card para lembretes
            item {
                RemindersPromptCard(onClick = onNavigateToReminders)
            }

            // Checklist diário
            item {
                DailyChecklistCard()
            }
        }
    }
}

@Composable
fun RoutineHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ModuleRoutine.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📅",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Crie uma Rotina Saudável",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bons hábitos de higiene bucal previnem 90% dos problemas dentários!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DailyRoutineCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = ModuleRoutine
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rotina Diária Ideal",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            RoutineTimeSlot(
                time = "Manhã ☀️",
                activities = listOf(
                    "Escovar os dentes após o pequeno-almoço (2-3 min)",
                    "Limpar a língua",
                    "Usar enxaguante bucal (opcional)"
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            RoutineTimeSlot(
                time = "Após Almoço 🍽️",
                activities = listOf(
                    "Escovar os dentes (2-3 min)",
                    "Usar fio dental se possível"
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            RoutineTimeSlot(
                time = "Noite 🌙",
                activities = listOf(
                    "Usar fio dental (2-3 min)",
                    "Escovar os dentes (2-3 min)",
                    "Limpar a língua",
                    "NÃO comer ou beber após escovar"
                )
            )
        }
    }
}

@Composable
fun RoutineTimeSlot(time: String, activities: List<String>) {
    Column {
        Text(
            text = time,
            style = MaterialTheme.typography.titleSmall,
            color = ModuleRoutine,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        activities.forEach { activity ->
            Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "• ",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = activity,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun HealthyHabitsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hábitos Saudáveis",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val habits = listOf(
                "💧 Beber bastante água ao longo do dia",
                "🥗 Comer frutas e vegetais crocantes",
                "🧀 Consumir alimentos ricos em cálcio",
                "🚰 Esperar 30 min após comer para escovar",
                "🔄 Trocar a escova a cada 3 meses",
                "👨‍⚕️ Visitar o dentista a cada 6 meses",
                "🦷 Usar pasta de dentes com flúor"
            )

            habits.forEach { habit ->
                Text(
                    text = habit,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HabitsToAvoidCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                    text = "Hábitos a Evitar",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val badHabits = listOf(
                "🍬 Consumo excessivo de açúcar",
                "🥤 Bebidas açucaradas e ácidas (refrigerantes)",
                "🚬 Tabagismo",
                "🧊 Roer gelo ou objetos duros",
                "✏️ Usar os dentes como ferramenta",
                "💪 Escovar com muita força",
                "⏰ Pular escovações"
            )

            badHabits.forEach { habit ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✗ ",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = habit,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun RemindersPromptCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ModuleRoutine
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Configure Lembretes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Receba notificações para não esquecer de escovar!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Abrir",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun DailyChecklistCard() {
    var morningBrushing by remember { mutableStateOf(false) }
    var afternoonBrushing by remember { mutableStateOf(false) }
    var nightBrushing by remember { mutableStateOf(false) }
    var flossing by remember { mutableStateOf(false) }
    var tongueCleaning by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Checklist,
                    contentDescription = null,
                    tint = ModuleRoutine
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Checklist de Hoje",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Marque o que você já fez hoje:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            ChecklistItem(
                text = "Escovação da manhã",
                checked = morningBrushing,
                onCheckedChange = { morningBrushing = it }
            )
            ChecklistItem(
                text = "Escovação após almoço",
                checked = afternoonBrushing,
                onCheckedChange = { afternoonBrushing = it }
            )
            ChecklistItem(
                text = "Escovação da noite",
                checked = nightBrushing,
                onCheckedChange = { nightBrushing = it }
            )
            ChecklistItem(
                text = "Fio dental",
                checked = flossing,
                onCheckedChange = { flossing = it }
            )
            ChecklistItem(
                text = "Limpeza da língua",
                checked = tongueCleaning,
                onCheckedChange = { tongueCleaning = it }
            )

            val completedCount = listOf(morningBrushing, afternoonBrushing, nightBrushing, flossing, tongueCleaning).count { it }

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { completedCount / 5f },
                modifier = Modifier.fillMaxWidth(),
                color = ModuleRoutine
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$completedCount de 5 tarefas completadas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ChecklistItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = ModuleRoutine
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (checked)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

