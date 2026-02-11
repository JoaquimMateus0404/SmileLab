package com.cleansoft.smilelab.ui.screens.viewer3d

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleansoft.smilelab.ui.components.FilamentViewer3D
import com.cleansoft.smilelab.ui.theme.SmilePrimary
import com.cleansoft.smilelab.ui.theme.SmileSecondary

/**
 * Tela de Visualização 3D dos Dentes
 * Usa Filament Engine para renderização 3D
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Teeth3DViewerScreen(
    onNavigateBack: () -> Unit
) {
    var hasError by remember { mutableStateOf(false) }
    var isModelLoaded by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Visualização 3D",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SmilePrimary.copy(alpha = 0.1f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Área do visualizador 3D com Filament
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (!hasError) {
                    FilamentViewer3D(
                        modifier = Modifier.fillMaxSize(),
                        modelPath = "models/helmet.glb",
                        onModelLoaded = {
                            android.util.Log.d("Teeth3DViewer", "✅ Modelo 3D carregado!")
                            isModelLoaded = true
                        },
                        onError = { error ->
                            android.util.Log.e("Teeth3DViewer", "❌ Erro ao carregar 3D", error)
                            hasError = true
                        }
                    )

                    // Instruções overlay (mostrar quando modelo estiver carregado)
                    if (isModelLoaded) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp),
                            tonalElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                InteractionHint(
                                    icon = Icons.Filled.TouchApp,
                                    text = "Arraste para rotacionar"
                                )
                                InteractionHint(
                                    icon = Icons.Filled.ZoomIn,
                                    text = "Pinça para zoom"
                                )
                            }
                        }
                    }
                }

                if (hasError) {
                    // Fallback UI quando o modelo não carrega
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        SmilePrimary.copy(alpha = 0.1f),
                                        SmileSecondary.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "🦷",
                                fontSize = 100.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Visualizador 3D em Desenvolvimento",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "A visualização 3D interativa dos dentes será implementada em breve com Filament Engine. Por enquanto, explore as outras funcionalidades do app!",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Como usar quando disponível:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    InteractionHint(
                                        icon = Icons.Filled.TouchApp,
                                        text = "Arraste para rotacionar"
                                    )
                                    InteractionHint(
                                        icon = Icons.Filled.ZoomIn,
                                        text = "Pinça para zoom"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Painel de controles
            ControlPanel()
        }
    }
}

@Composable
fun InteractionHint(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = SmilePrimary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun ControlPanel() {
    var selectedView by remember { mutableStateOf("full") }
    var showLabels by remember { mutableStateOf(true) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Seletor de visualização
            Text(
                text = "Visualização",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ViewButton(
                    text = "Completa",
                    selected = selectedView == "full",
                    onClick = { selectedView = "full" },
                    modifier = Modifier.weight(1f)
                )
                ViewButton(
                    text = "Superior",
                    selected = selectedView == "upper",
                    onClick = { selectedView = "upper" },
                    modifier = Modifier.weight(1f)
                )
                ViewButton(
                    text = "Inferior",
                    selected = selectedView == "lower",
                    onClick = { selectedView = "lower" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle de labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mostrar nomes dos dentes",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = showLabels,
                    onCheckedChange = { showLabels = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SmilePrimary,
                        checkedTrackColor = SmilePrimary.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão de reset
            OutlinedButton(
                onClick = { /* Reset view */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Resetar visualização")
            }
        }
    }
}

@Composable
fun ViewButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SmilePrimary
            )
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

