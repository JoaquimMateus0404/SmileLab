package com.cleansoft.smilelab.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = "Permissão de Notificações",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "O SmileLab precisa de permissão para enviar notificações e lembretes de escovação.\n\n" +
                        "Isso ajudará você a manter uma rotina saudável de higiene bucal!"
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Permitir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Agora não")
            }
        }
    )
}

@Composable
fun AlarmPermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = "Permissão de Alarmes",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Para que os lembretes funcionem corretamente, o SmileLab precisa de permissão para agendar alarmes exatos.\n\n" +
                        "Você será redirecionado para as configurações do sistema."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Abrir Configurações")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

