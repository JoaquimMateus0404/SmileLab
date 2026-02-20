package com.cleansoft.smilelab.ui.screens.gallery3d

import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.cleansoft.smilelab.data.model.TeethModel3D
import com.cleansoft.smilelab.data.model.TeethModelType
import com.cleansoft.smilelab.ui.theme.SmilePrimary
import com.cleansoft.smilelab.ui.theme.SmileSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gallery3DScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Apenas tipos que são imagens (is3D == false)
    val imageTypes = remember {
        TeethModelType.entries.filter { !it.is3D }
    }

    // initial model: first image if available
    var selectedModel by remember {
        mutableStateOf(TeethModel3D(imageTypes.first()))
    }

    var showInfo by remember { mutableStateOf(false) }
    val allModels = remember {
        imageTypes.map { TeethModel3D(it) }
    }

    // Transform state for gestures
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // reset transform when model changes
    LaunchedEffect(selectedModel.fileName) {
        scale = 1f
        offsetX = 0f
        offsetY = 0f
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
            // Área do visualizador (exibir imagens)
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
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Carrega imagem do assets/models
                val bitmap = remember(selectedModel.fileName) {
                    try {
                        context.assets.open("models/${selectedModel.fileName}").use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    } catch (_: Exception) {
                        null
                    }
                }

                // Area interativa com gestos: pinça para zoom e arraste para mover
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .pointerInput(selectedModel.fileName) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                // pan: Offset in pixels
                                offsetX += pan.x
                                offsetY += pan.y
                                scale = (scale * zoom).coerceIn(0.5f, 4f)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = selectedModel.displayName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationX = offsetX
                                    translationY = offsetY
                                    scaleX = scale
                                    scaleY = scale
                                },
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = selectedModel.previewEmoji,
                            fontSize = 120.sp
                        )
                    }

                    // dica de uso flutuante
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Use pinça para zoom e arraste para mover",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Botões utilitários (Reset / Fit)
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Reset")
                        }

                        FilledTonalIconButton(
                            onClick = {
                                // Ajusta para caber na largura (simplesmente restaura escala 1)
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(Icons.Filled.ZoomIn, contentDescription = "Ajustar")
                        }
                    }
                }

                if (showInfo) {
                    ModelInfoCard(selectedModel)
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

            // Paleta de seleção de imagens (miniaturas carregadas dos assets)
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
                // Tentar carregar miniatura do assets
                val context = LocalContext.current
                val thumbBitmap = remember(model.fileName) {
                    try {
                        context.assets.open("models/${model.fileName}").use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    } catch (_: Exception) {
                        null
                    }
                }

                if (thumbBitmap != null) {
                    Image(
                        bitmap = thumbBitmap.asImageBitmap(),
                        contentDescription = model.displayName,
                        modifier = Modifier.size(36.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = model.previewEmoji,
                        fontSize = 28.sp
                    )
                }
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

            // Informações adicionais baseadas no tipo (apenas para imagens)
            Spacer(modifier = Modifier.height(12.dp))

            if (!model.is3D) {
                when (model.type) {
                    TeethModelType.MOLAR_IMAGE -> {
                        InfoItem("Função", "Triturar e moer alimentos")
                        InfoItem("Localização", "Parte posterior da boca")
                        InfoItem("Curiosidade", "Os molares têm várias cúspides para triturar alimentos")
                    }
                    TeethModelType.INCISOR_IMAGE -> {
                        InfoItem("Função", "Cortar alimentos")
                        InfoItem("Localização", "Frente da boca")
                        InfoItem("Curiosidade", "Incisivos têm bordas afiadas para cortar")
                    }
                    TeethModelType.CANINE_IMAGE -> {
                        InfoItem("Função", "Rasgar alimentos")
                        InfoItem("Localização", "Entre incisivos e pré-molares")
                        InfoItem("Curiosidade", "Caninos são mais pontiagudos e fortes")
                    }
                    TeethModelType.PREMOLAR_IMAGE -> {
                        InfoItem("Função", "Triturar e rasgar")
                        InfoItem("Localização", "Entre caninos e molares")
                    }
                    TeethModelType.TOOTH_WITH_DECAY_IMAGE -> {
                        InfoItem("Atenção", "Apresenta sinais de cárie")
                        InfoItem("Recomendação", "Procure um dentista para avaliação")
                    }
                    TeethModelType.GINGIVITIS_IMAGE -> {
                        InfoItem("Descrição", "Inflamação das gengivas")
                        InfoItem("Prevenção", "Higiene bucal adequada e consulta regular")
                    }
                    TeethModelType.PERIODONTITIS_IMAGE -> {
                        InfoItem("Descrição", "Inflamação avançada que afeta os tecidos de suporte")
                        InfoItem("Prevenção", "Controle de placa e acompanhamento profissional")
                    }
                    TeethModelType.DENTAL_ANATOMY_IMAGE, TeethModelType.DENTAL_STRUCTURE_IMAGE -> {
                        InfoItem("Uso", "Referência anatômica educativa")
                        InfoItem("Sugestão", "Leia o conteúdo relacionado na seção Conhecer Dentes")
                    }
                    else -> {
                        InfoItem("Tipo", model.displayName)
                    }
                }
            } else {
                // Para modelos 3D, apenas mostrar que é um modelo 3D e o arquivo
                InfoItem("Formato", "Modelo 3D (.glb)")
                InfoItem("Arquivo", model.fileName)
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
