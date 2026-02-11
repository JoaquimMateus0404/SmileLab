package com.cleansoft.smilelab.ui.screens.gallery3d

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleansoft.smilelab.data.model.TeethModel3D
import com.cleansoft.smilelab.data.model.TeethModelType
import com.cleansoft.smilelab.ui.theme.SmilePrimary
import com.cleansoft.smilelab.ui.theme.SmileSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gallery3DScreen(
    onNavigateBack: () -> Unit
) {
    var selectedModel by remember {
        mutableStateOf(TeethModel3D(TeethModelType.COMPLETE_DENTITION))
    }
    var showInfo by remember { mutableStateOf(false) }
    val allModels = remember {
        TeethModelType.entries.map { TeethModel3D(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Galeria 3D",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfo = !showInfo }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Informações",
                            tint = if (showInfo) SmilePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Área do visualizador 3D
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SmilePrimary.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                // TODO: Integrar FilamentViewer3D com o modelo selecionado
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = selectedModel.icon,
                        fontSize = 120.sp,
                        modifier = Modifier
                            .graphicsLayer {
                                rotationY = 360f
                            }
                    )

                    AnimatedVisibility(visible = showInfo) {
                        ModelInfoCard(selectedModel)
                    }
                }

                // Controles de rotação (simulação)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ControlButton(
                        icon = Icons.AutoMirrored.Filled.RotateLeft,
                        text = "Girar Esq"
                    )
                    ControlButton(
                        icon = Icons.Filled.ZoomIn,
                        text = "Zoom +"
                    )
                    ControlButton(
                        icon = Icons.Filled.ZoomOut,
                        text = "Zoom -"
                    )
                    ControlButton(
                        icon = Icons.AutoMirrored.Filled.RotateRight,
                        text = "Girar Dir"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título do modelo selecionado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedModel.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SmilePrimary
                )
                Text(
                    text = selectedModel.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Paleta de seleção de modelos
            ModelPalette(
                models = allModels,
                selectedModel = selectedModel,
                onModelSelected = { selectedModel = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ModelPalette(
    models: List<TeethModel3D>,
    selectedModel: TeethModel3D,
    onModelSelected: (TeethModel3D) -> Unit
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Modelos Disponíveis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${models.indexOf(selectedModel) + 1}/${models.size}",
                style = MaterialTheme.typography.bodySmall,
                color = SmilePrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(models) { model ->
                ModelPaletteItem(
                    model = model,
                    isSelected = model.type == selectedModel.type,
                    onClick = { onModelSelected(model) }
                )
            }
        }
    }
}

@Composable
fun ModelPaletteItem(
    model: TeethModel3D,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                SmilePrimary.copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            Brush.linearGradient(
                                colors = listOf(SmilePrimary, SmileSecondary)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = model.icon,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.displayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = if (isSelected) SmilePrimary else MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(SmilePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalIconButton(
            onClick = { /* TODO: Implementar controles */ },
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = SmilePrimary.copy(alpha = 0.2f),
                contentColor = SmilePrimary
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ModelInfoCard(model: TeethModel3D) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = SmilePrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sobre este modelo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            // Informações adicionais baseadas no tipo
            Spacer(modifier = Modifier.height(12.dp))

            when (model.type) {
                TeethModelType.MOLAR -> {
                    InfoItem("Função", "Triturar e moer alimentos")
                    InfoItem("Localização", "Parte posterior da boca")
                    InfoItem("Quantidade", "12 molares (incluindo sisos)")
                }
                TeethModelType.INCISOR -> {
                    InfoItem("Função", "Cortar alimentos")
                    InfoItem("Localização", "Frente da boca")
                    InfoItem("Quantidade", "8 incisivos")
                }
                TeethModelType.CANINE -> {
                    InfoItem("Função", "Rasgar alimentos")
                    InfoItem("Localização", "Entre incisivos e pré-molares")
                    InfoItem("Quantidade", "4 caninos")
                }
                else -> {
                    InfoItem("Tipo", model.displayName)
                }
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

