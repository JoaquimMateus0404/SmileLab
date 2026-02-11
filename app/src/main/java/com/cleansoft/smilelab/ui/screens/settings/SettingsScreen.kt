package com.cleansoft.smilelab.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cleansoft.smilelab.data.repository.UserPreferencesRepository
import com.cleansoft.smilelab.ui.theme.SmilePrimary
import kotlinx.coroutines.launch

/**
 * Tela de Configurações
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    userPreferencesRepository: UserPreferencesRepository
) {
    val coroutineScope = rememberCoroutineScope()

    // Coletar preferências do repository
    val darkModeEnabled by userPreferencesRepository.darkModeEnabled.collectAsState(initial = false)
    val notificationsEnabled by userPreferencesRepository.notificationsEnabled.collectAsState(initial = true)
    val soundEnabled by userPreferencesRepository.soundEnabled.collectAsState(initial = true)
    val vibrationEnabled by userPreferencesRepository.vibrationEnabled.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚙️ Configurações",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
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
            // Secção de Aparência
            item {
                SettingsSection(title = "Aparência") {
                    SettingsSwitchItem(
                        icon = Icons.Outlined.DarkMode,
                        title = "Modo Escuro",
                        subtitle = "Ativar tema escuro",
                        checked = darkModeEnabled,
                        onCheckedChange = {
                            coroutineScope.launch {
                                userPreferencesRepository.setDarkModeEnabled(it)
                            }
                        }
                    )
                }
            }

            // Secção de Notificações
            item {
                SettingsSection(title = "Notificações") {
                    SettingsSwitchItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Notificações",
                        subtitle = "Receber lembretes de escovação",
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            coroutineScope.launch {
                                userPreferencesRepository.setNotificationsEnabled(it)
                            }
                        }
                    )

                    if (notificationsEnabled) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        SettingsSwitchItem(
                            icon = Icons.AutoMirrored.Outlined.VolumeUp,
                            title = "Som",
                            subtitle = "Ativar som nas notificações",
                            checked = soundEnabled,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    userPreferencesRepository.setSoundEnabled(it)
                                }
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        SettingsSwitchItem(
                            icon = Icons.Outlined.Vibration,
                            title = "Vibração",
                            subtitle = "Ativar vibração nas notificações",
                            checked = vibrationEnabled,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    userPreferencesRepository.setVibrationEnabled(it)
                                }
                            }
                        )
                    }
                }
            }

            // Secção Sobre
            item {
                SettingsSection(title = "Sobre") {
                    SettingsClickItem(
                        icon = Icons.Outlined.Info,
                        title = "Sobre o SmileLab",
                        subtitle = "Versão 1.0.0",
                        onClick = { /* TODO */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsClickItem(
                        icon = Icons.Outlined.Description,
                        title = "Termos de Uso",
                        subtitle = null,
                        onClick = { /* TODO */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsClickItem(
                        icon = Icons.Outlined.PrivacyTip,
                        title = "Política de Privacidade",
                        subtitle = null,
                        onClick = { /* TODO */ }
                    )
                }
            }

            // Secção de Dados
            item {
                SettingsSection(title = "Dados") {
                    SettingsClickItem(
                        icon = Icons.Outlined.Refresh,
                        title = "Resetar Progresso",
                        subtitle = "Reiniciar todo o progresso de aprendizagem",
                        onClick = { /* TODO: Mostrar diálogo de confirmação */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsClickItem(
                        icon = Icons.Outlined.DeleteForever,
                        title = "Limpar Dados",
                        subtitle = "Apagar todos os dados do aplicativo",
                        onClick = { /* TODO: Mostrar diálogo de confirmação */ },
                        isDestructive = true
                    )
                }
            }

            // Disclaimer
            item {
                DisclaimerSettingsCard()
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = SmilePrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SmilePrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SmilePrimary,
                checkedTrackColor = SmilePrimary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) MaterialTheme.colorScheme.error else SmilePrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDestructive)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun DisclaimerSettingsCard() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.MedicalServices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "O SmileLab é um app educativo e não substitui a consulta a um profissional de saúde bucal.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

