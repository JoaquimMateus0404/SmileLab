package com.cleansoft.smilelab.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cleansoft.smilelab.data.local.SmileLabDatabase
import com.cleansoft.smilelab.data.repository.ProgressStats
import com.cleansoft.smilelab.data.repository.UserProgressRepository
import com.cleansoft.smilelab.ui.components.UserProgressCard
import com.cleansoft.smilelab.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Dados para os módulos educativos
 */
data class EducationalModule(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val color: Color,
    val icon: ImageVector
)

val educationalModules = listOf(
    EducationalModule(
        id = "know_teeth",
        emoji = "🦷",
        title = "Conhecer os Dentes",
        description = "Anatomia dental e tipos de dentes",
        color = ModuleKnowTeeth,
        icon = Icons.Outlined.Info
    ),
    EducationalModule(
        id = "hygiene",
        emoji = "🪥",
        title = "Higiene Bucal",
        description = "Técnicas de escovação e limpeza",
        color = ModuleHygiene,
        icon = Icons.Outlined.CleaningServices
    ),
    EducationalModule(
        id = "problems",
        emoji = "⚠️",
        title = "Problemas Dentários",
        description = "Cáries, gengivite e prevenção",
        color = ModuleProblems,
        icon = Icons.Outlined.Warning
    ),
    EducationalModule(
        id = "routine",
        emoji = "📅",
        title = "Rotina & Hábitos",
        description = "Crie hábitos saudáveis",
        color = ModuleRoutine,
        icon = Icons.Outlined.Schedule
    )
)

/**
 * Home Screen - Tela principal do SmileLab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToKnowTeeth: () -> Unit,
    onNavigateToOralHygiene: () -> Unit,
    onNavigateToDentalProblems: () -> Unit,
    onNavigateToRoutineHabits: () -> Unit,
    onNavigateTo3DViewer: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State para estatísticas de progresso
    var progressStats by remember {
        mutableStateOf(
            ProgressStats(
                totalViewed = 0,
                totalCompleted = 0,
                completionPercentage = 0,
                categoriesProgress = emptyMap()
            )
        )
    }

    // Carregar estatísticas ao iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            val database = SmileLabDatabase.getDatabase(context)
            val repository = UserProgressRepository(database.userProgressDao())
            progressStats = repository.getProgressStats()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🦷",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "SmileLab",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
            // Banner de boas-vindas
            item {
                WelcomeBanner()
            }

            // Card de progresso do utilizador
            item {
                UserProgressCard(progressStats = progressStats)
            }

            // Card de visualização 3D
            item {
                Feature3DCard(onClick = onNavigateTo3DViewer)
            }

            // Título dos módulos
            item {
                Text(
                    text = "Módulos Educativos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Grid de módulos educativos
            item {
                ModulesGrid(
                    onNavigateToKnowTeeth = onNavigateToKnowTeeth,
                    onNavigateToOralHygiene = onNavigateToOralHygiene,
                    onNavigateToDentalProblems = onNavigateToDentalProblems,
                    onNavigateToRoutineHabits = onNavigateToRoutineHabits
                )
            }

            // Card de lembretes
            item {
                RemindersQuickCard(onClick = onNavigateToReminders)
            }

            // Disclaimer
            item {
                DisclaimerCard()
            }
        }
    }
}

@Composable
fun WelcomeBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Olá! 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pronto para aprender sobre saúde bucal?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Text(
                text = "😄",
                fontSize = 48.sp
            )
        }
    }
}

@Composable
fun Feature3DCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SmilePrimary,
                            SmileSecondary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ViewInAr,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Visualização 3D",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Explore a boca e os dentes em 3D interativo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Abrir",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun ModulesGrid(
    onNavigateToKnowTeeth: () -> Unit,
    onNavigateToOralHygiene: () -> Unit,
    onNavigateToDentalProblems: () -> Unit,
    onNavigateToRoutineHabits: () -> Unit
) {
    val moduleActions = mapOf(
        "know_teeth" to onNavigateToKnowTeeth,
        "hygiene" to onNavigateToOralHygiene,
        "problems" to onNavigateToDentalProblems,
        "routine" to onNavigateToRoutineHabits
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            educationalModules.take(2).forEach { module ->
                ModuleCard(
                    module = module,
                    onClick = { moduleActions[module.id]?.invoke() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            educationalModules.drop(2).forEach { module ->
                ModuleCard(
                    module = module,
                    onClick = { moduleActions[module.id]?.invoke() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ModuleCard(
    module: EducationalModule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = module.color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = module.emoji,
                fontSize = 40.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = module.color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = module.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2
            )
        }
    }
}

@Composable
fun RemindersQuickCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Lembretes de Escovação",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configure alertas para manter sua rotina",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Abrir",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun DisclaimerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Este app é educativo e não substitui a consulta a um dentista profissional.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

