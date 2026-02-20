package com.cleansoft.smilelab.ui.screens.gallery3d

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.animation.*
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.cleansoft.smilelab.data.model.TeethModel3D
import com.cleansoft.smilelab.data.model.TeethModelType
import com.cleansoft.smilelab.ui.theme.SmilePrimary
import com.cleansoft.smilelab.ui.theme.SmileSecondary

private sealed interface LoadResult {
    object Loading : LoadResult
    data class Success(val bitmap: Bitmap) : LoadResult
    object Error : LoadResult
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

private suspend fun decodeBitmapFromAsset(context: Context, assetPath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.assets.open(assetPath).use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            context.assets.open(assetPath).use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
        } catch (_: Exception) {
            null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gallery3DScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // LruCache (em KB)
    val bitmapCache = remember {
        val maxMemoryKb = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        // Use 1/8th of available memory for cache
        val cacheSizeKb = maxMemoryKb / 8
        object : LruCache<String, Bitmap>(cacheSizeKb) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    // Apenas tipos que são imagens (is3D == false)
    val imageTypes = remember { TeethModelType.entries.filter { !it.is3D } }

    // initial model: first image if available (fallback to CANINE_IMAGE)
    val defaultType = imageTypes.firstOrNull() ?: TeethModelType.CANINE_IMAGE
    var selectedModel by remember { mutableStateOf(TeethModel3D(defaultType)) }

    val showInfoState = remember { mutableStateOf(false) }
    val allModels = remember(imageTypes) { imageTypes.map { TeethModel3D(it) } }

    // Transform state for gestures: baseScale (fit-to-screen) x userScale (pinch)
    var baseScale by remember { mutableStateOf(1f) }
    var userScale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // When model changes, reset user transform but keep baseScale recalculated later
    LaunchedEffect(selectedModel.fileName) {
        userScale = 1f
        offsetX = 0f
        offsetY = 0f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Galeria de imagens", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoState.value = !showInfoState.value }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Informações",
                            tint = if (showInfoState.value) SmilePrimary else MaterialTheme.colorScheme.onSurface
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
            // Visualizador: usar Box + onGloballyPositioned para obter tamanho disponível em pixels
            var containerWidthPx by remember { mutableStateOf(0f) }
            var containerHeightPx by remember { mutableStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Brush.verticalGradient(colors = listOf(SmilePrimary.copy(alpha = 0.05f), Color.Transparent)))
                    .onGloballyPositioned { coords ->
                        val size = coords.size
                        containerWidthPx = size.width.toFloat()
                        containerHeightPx = size.height.toFloat()
                    },
                contentAlignment = Alignment.Center
            ) {
                // Async load with produceState and caching
                val loadResult by produceState<LoadResult>(initialValue = LoadResult.Loading, key1 = selectedModel.fileName) {
                    val cacheKey = "main:${selectedModel.fileName}"
                    // try cache
                    val cached = bitmapCache.get(cacheKey)
                    if (cached != null) {
                        value = LoadResult.Success(cached)
                        return@produceState
                    }

                    // decode with a reasonable cap: target max dimension equals container * 1.5 (allow zoom quality)
                    val reqW = (containerWidthPx * 1.5f).toInt().coerceAtLeast(256)
                    val reqH = (containerHeightPx * 1.5f).toInt().coerceAtLeast(256)

                    val bmp = decodeBitmapFromAsset(context, "models/${selectedModel.fileName}", reqW, reqH)
                    if (bmp != null) {
                        bitmapCache.put(cacheKey, bmp)
                        value = LoadResult.Success(bmp)
                    } else {
                        value = LoadResult.Error
                    }
                }

                // compute baseScale once when bitmap becomes available
                LaunchedEffect(loadResult) {
                    if (loadResult is LoadResult.Success) {
                        val bmp = (loadResult as LoadResult.Success).bitmap
                        if (bmp.width > 0 && bmp.height > 0) {
                            val fitScale = minOf(containerWidthPx / bmp.width.toFloat(), containerHeightPx / bmp.height.toFloat())
                            baseScale = fitScale.coerceAtMost(1f)
                        } else {
                            baseScale = 1f
                        }
                        userScale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    }
                }

                // Combined scale used for rendering
                val combinedScale by remember(baseScale, userScale) { derivedStateOf { baseScale * userScale } }

                // Crossfade between loading/success/error
                Crossfade(targetState = loadResult, label = "imageCrossfade") { result ->
                    when (result) {
                        is LoadResult.Loading -> {
                            // show loading indicator
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is LoadResult.Error -> {
                            // fallback friendly UI
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.BrokenImage, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Imagem indisponível", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }
                        is LoadResult.Success -> {
                            val bmp = result.bitmap
                            // Interactive area
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(selectedModel.fileName) {
                                        detectTransformGestures { _, pan, zoom, _ ->
                                            // update pan and zoom (userScale)
                                            offsetX += pan.x
                                            offsetY += pan.y
                                            userScale = (userScale * zoom).coerceIn(0.5f, 6f)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = selectedModel.displayName,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            translationX = offsetX
                                            translationY = offsetY
                                            scaleX = combinedScale
                                            scaleY = combinedScale
                                        }
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Fit
                                )

                                // floating usage hint
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 8.dp)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Pinça: zoom — Arraste: mover",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // util buttons
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    FilledTonalIconButton(
                                        onClick = {
                                            // reset user transform and fit
                                            userScale = 1f
                                            offsetX = 0f
                                            offsetY = 0f
                                        },
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(Icons.Filled.Refresh, contentDescription = "Reset")
                                    }

                                    FilledTonalIconButton(
                                        onClick = {
                                            // fit to screen by recalculating baseScale (already fit) and reset userScale
                                            userScale = 1f
                                            offsetX = 0f
                                            offsetY = 0f
                                        },
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(Icons.Filled.ZoomIn, contentDescription = "Ajustar")
                                    }
                                }
                            }
                        }
                    }
                }

                // Animated info card (usa o state com .value)
                Crossfade(targetState = showInfoState.value, label = "infoCrossfade") { visible ->
                    if (visible) {
                        // posiciona o card no topo do visualizador
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.TopCenter)
                        ) {
                            ModelInfoCard(selectedModel)
                        }
                    }
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
                Text(text = selectedModel.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = SmilePrimary)
                Text(text = selectedModel.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Paleta de seleção (com downsampling + cache)
            ModelPalette(models = allModels, selectedModel = selectedModel, onModelSelected = { selectedModel = it }, bitmapCache = bitmapCache, density = density)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ModelPalette(
    models: List<TeethModel3D>,
    selectedModel: TeethModel3D,
    onModelSelected: (TeethModel3D) -> Unit,
    bitmapCache: LruCache<String, Bitmap>,
    density: Density
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Modelos Disponíveis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "${models.indexOf(selectedModel) + 1}/${models.size}", style = MaterialTheme.typography.bodySmall, color = SmilePrimary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(state = listState, contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(models) { model ->
                ModelPaletteItem(model = model, isSelected = model.type == selectedModel.type, onClick = { onModelSelected(model) }, bitmapCache = bitmapCache, density = density)
            }
        }
    }
}

@Composable
private fun ModelPaletteItem(
    model: TeethModel3D,
    isSelected: Boolean,
    onClick: () -> Unit,
    bitmapCache: LruCache<String, Bitmap>,
    density: Density
) {
    val scaleAnim by animateFloatAsState(targetValue = if (isSelected) 1.1f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "scale")

    Card(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp)
            .graphicsLayer { scaleX = scaleAnim; scaleY = scaleAnim },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) SmilePrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isSelected) Brush.linearGradient(colors = listOf(SmilePrimary, SmileSecondary)) else Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface))), contentAlignment = Alignment.Center) {
                val context = LocalContext.current
                // thumbnail cache key
                val cacheKey = "thumb:${model.fileName}"
                val thumbState by produceState<LoadResult>(initialValue = LoadResult.Loading, key1 = model.fileName) {
                    val cached = bitmapCache.get(cacheKey)
                    if (cached != null) { value = LoadResult.Success(cached); return@produceState }
                    // request small thumbnail (in pixels) - use 72dp as target
                    val targetPx = with(density) { 72.dp.toPx().toInt() }
                    val bmp = decodeBitmapFromAsset(context, "models/${model.fileName}", targetPx, targetPx)
                    if (bmp != null) {
                        bitmapCache.put(cacheKey, bmp)
                        value = LoadResult.Success(bmp)
                    } else value = LoadResult.Error
                }

                when (thumbState) {
                    is LoadResult.Loading -> CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    is LoadResult.Error -> Text(text = model.previewEmoji, fontSize = 20.sp)
                    is LoadResult.Success -> Image(bitmap = (thumbState as LoadResult.Success).bitmap.asImageBitmap(), contentDescription = model.displayName, modifier = Modifier.size(36.dp), contentScale = ContentScale.Crop)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = model.displayName, style = MaterialTheme.typography.labelSmall, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center, maxLines = 2, color = if (isSelected) SmilePrimary else MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
        }

        if (isSelected) {
            Box(modifier = Modifier.align(Alignment.End).padding(4.dp).size(20.dp).clip(CircleShape).background(SmilePrimary), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
fun ModelInfoCard(model: TeethModel3D) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = SmilePrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sobre este modelo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = model.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

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
                InfoItem("Formato", "Modelo 3D (.glb)")
                InfoItem("Arquivo", model.fileName)
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
