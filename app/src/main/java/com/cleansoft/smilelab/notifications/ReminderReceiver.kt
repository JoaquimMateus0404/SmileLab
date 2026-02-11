package com.cleansoft.smilelab.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cleansoft.smilelab.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receiver para disparar notificações de lembretes
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, -1)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "🦷 Hora de Escovar!"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Não se esqueça de cuidar do seu sorriso!"

        if (reminderId == -1) return

        // Verificar preferências do usuário
        val userPreferencesRepository = UserPreferencesRepository(context)

        CoroutineScope(Dispatchers.IO).launch {
            val notificationsEnabled = userPreferencesRepository.notificationsEnabled.first()

            if (notificationsEnabled) {
                val soundEnabled = userPreferencesRepository.soundEnabled.first()
                val vibrationEnabled = userPreferencesRepository.vibrationEnabled.first()

                NotificationHelper.showBrushingReminder(
                    context = context,
                    notificationId = reminderId,
                    title = title,
                    message = message,
                    enableSound = soundEnabled,
                    enableVibration = vibrationEnabled
                )
            }
        }
    }

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_MESSAGE = "message"
    }
}

