package com.cleansoft.smilelab.ui.screens.viewer3d

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cleansoft.smilelab.data.model.TeethModelType
import com.cleansoft.smilelab.filament.FilamentSceneManager
import com.cleansoft.smilelab.ui.components.FilamentViewer3D
import com.cleansoft.smilelab.ui.theme.SmilePrimary
import com.cleansoft.smilelab.ui.theme.SmileSecondary

private data class ViewerModelOption(
    val id: String,
    val displayName: String,
    val modelPath: String
)

private val viewerModelOptions = listOf(

    ViewerModelOption(
        "incisor",
        TeethModelType.MAXILLARY_LEFT_CENTRAL_INCISOR.displayName,
        "models/${TeethModelType.MAXILLARY_LEFT_CENTRAL_INCISOR.fileName}"
    ),
    ViewerModelOption(
        "permanent",
        TeethModelType.PERMANENT_DENTITION.displayName,
        "models/${TeethModelType.PERMANENT_DENTITION.fileName}"
    ),
    ViewerModelOption(
        "canine",
        TeethModelType.MAXILLARY_CANINE.displayName,
        "models/${TeethModelType.MAXILLARY_CANINE.fileName}"
    ),
    ViewerModelOption(
        "molar",
        TeethModelType.MAXILLARY_FIRST_MOLAR.displayName,
        "models/${TeethModelType.MAXILLARY_FIRST_MOLAR.fileName}"
    ),
    ViewerModelOption(
        "premolar",
        TeethModelType.MANDIBULAR_FIRST_PREMOLAR.displayName,
        "models/${TeethModelType.MANDIBULAR_FIRST_PREMOLAR.fileName}"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Teeth3DViewerScreen(
    onNavigateBack: () -> Unit
) {
    var hasError by remember { mutableStateOf(false) }
    var isModelLoaded by remember { mutableStateOf(false) }
    var sceneManager by remember { mutableStateOf<FilamentSceneManager?>(null) }
    var selectedModel by remember { mutableStateOf(viewerModelOptions.first()) }
    var mainLightIntensity by remember { mutableFloatStateOf(25_000f) }
    var ambientLightIntensity by remember { mutableFloatStateOf(12_000f) }
    var skyboxTone by remember { mutableFloatStateOf(0.4f) }
    var autoRotateHint by remember { mutableStateOf(true) }

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Visualização 3D", fontWeight = FontWeight.Bold) },
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (!hasError) {
                    FilamentViewer3D(
                        modifier = Modifier.fillMaxSize(),
                        modelPath = selectedModel.modelPath,
                        onModelLoaded = {
                            isModelLoaded = true
                            hasError = false
                        },
                        onError = {
                            hasError = true
                        },
                        onSceneManagerReady = { manager ->
                            sceneManager = manager
                            manager.setMainLightIntensity(mainLightIntensity)
                            manager.setIndirectLightIntensity(ambientLightIntensity)
                            manager.setSkyboxColor(skyboxTone, skyboxTone, (skyboxTone + 0.05f).coerceAtMost(1f))
                        }
                    )

                    if (isModelLoaded) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FloatingActionButton(
                                onClick = { sceneManager?.getCameraManipulator()?.scroll(0, 0, -2f) },
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                contentColor = SmilePrimary,
                                modifier = Modifier.size(48.dp)
                            ) { Icon(Icons.Filled.Add, contentDescription = "Zoom In") }

                            FloatingActionButton(
                                onClick = { sceneManager?.getCameraManipulator()?.scroll(0, 0, 2f) },
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                contentColor = SmilePrimary,
                                modifier = Modifier.size(48.dp)
                            ) { Icon(Icons.Filled.Remove, contentDescription = "Zoom Out") }

                            FloatingActionButton(
                                onClick = { sceneManager?.resetCamera() },
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                contentColor = SmilePrimary,
                                modifier = Modifier.size(48.dp)
                            ) { Icon(Icons.Filled.Refresh, contentDescription = "Reset") }
                        }

                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(8.dp),
                            tonalElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                InteractionHint(icon = Icons.Filled.TouchApp, text = "1 dedo: rotacionar")
                                InteractionHint(icon = Icons.Filled.ZoomIn, text = "Pinça: zoom")
                                InteractionHint(icon = Icons.Filled.PanTool, text = "2 dedos: mover")
                            }
                        }
                    }
                }

                if (hasError) {
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
                        Text(
                            text = "Não foi possível carregar o modelo 3D selecionado.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }

            ViewerControlPanel(
                selectedModel = selectedModel,
                onModelSelected = {
                    selectedModel = it
                    isModelLoaded = false
                },
                mainLightIntensity = mainLightIntensity,
                onMainLightIntensityChange = {
                    mainLightIntensity = it
                    sceneManager?.setMainLightIntensity(it)
                },
                ambientLightIntensity = ambientLightIntensity,
                onAmbientLightIntensityChange = {
                    ambientLightIntensity = it
                    sceneManager?.setIndirectLightIntensity(it)
                },
                skyboxTone = skyboxTone,
                onSkyboxToneChange = {
                    skyboxTone = it
                    sceneManager?.setSkyboxColor(it, it, (it + 0.05f).coerceAtMost(1f))
                },
                autoRotateHint = autoRotateHint,
                onAutoRotateHintChange = { autoRotateHint = it },
                onReset = {
                    sceneManager?.resetCamera()
                    mainLightIntensity = 25_000f
                    ambientLightIntensity = 12_000f
                    skyboxTone = 0.4f
                    sceneManager?.setMainLightIntensity(mainLightIntensity)
                    sceneManager?.setIndirectLightIntensity(ambientLightIntensity)
                    sceneManager?.setSkyboxColor(skyboxTone, skyboxTone, (skyboxTone + 0.05f).coerceAtMost(1f))
                }
            )
        }
    }
}

@Composable
private fun ViewerControlPanel(
    selectedModel: ViewerModelOption,
    modelOptions: List<ViewerModelOption> = viewerModelOptions,
    onModelSelected: (ViewerModelOption) -> Unit,
    mainLightIntensity: Float,
    onMainLightIntensityChange: (Float) -> Unit,
    ambientLightIntensity: Float,
    onAmbientLightIntensityChange: (Float) -> Unit,
    skyboxTone: Float,
    onSkyboxToneChange: (Float) -> Unit,
    autoRotateHint: Boolean,
    onAutoRotateHintChange: (Boolean) -> Unit,
    onReset: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Modelos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(modelOptions, key = { it.id }) { option ->
                    val selected = option.id == selectedModel.id
                    AssistChip(
                        onClick = { onModelSelected(option) },
                        label = { Text(option.displayName) },
                        leadingIcon = {
                            if (selected) {
                                Icon(Icons.Filled.Refresh, contentDescription = null)
                            }
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selected) SmilePrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            ControlSlider(
                icon = Icons.Filled.WbSunny,
                title = "Luz principal",
                value = mainLightIntensity,
                valueRange = 5_000f..80_000f,
                onValueChange = onMainLightIntensityChange
            )

            ControlSlider(
                icon = Icons.Filled.Palette,
                title = "Luz ambiente",
                value = ambientLightIntensity,
                valueRange = 2_000f..30_000f,
                onValueChange = onAmbientLightIntensityChange
            )

            ControlSlider(
                icon = Icons.Filled.Palette,
                title = "Cor de fundo",
                value = skyboxTone,
                valueRange = 0.1f..0.8f,
                onValueChange = onSkyboxToneChange
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dicas de interação", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = autoRotateHint,
                    onCheckedChange = onAutoRotateHintChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SmilePrimary,
                        checkedTrackColor = SmilePrimary.copy(alpha = 0.5f)
                    )
                )
            }

            androidx.compose.material3.OutlinedButton(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text("Resetar câmera e luz")
            }
        }
    }
}

@Composable
private fun ControlSlider(
    icon: ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = SmilePrimary, modifier = Modifier.size(16.dp))
            Text(title, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(value.toInt().toString(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange)
    }
}

@Composable
fun InteractionHint(
    icon: ImageVector,
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
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}
