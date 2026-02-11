package com.cleansoft.smilelab.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cleansoft.smilelab.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receiver para disparar notificações de lembretes
 */
class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_MESSAGE = "message"
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, -1)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "🦷 Hora de Escovar!"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Não se esqueça de cuidar do seu sorriso!"

        Log.d(TAG, "🔔 Recebido lembrete #$reminderId")

        if (reminderId == -1) {
            Log.e(TAG, "❌ ID de lembrete inválido!")
            return
        }

        // Verificar preferências do usuário
        val userPreferencesRepository = UserPreferencesRepository(context)

        CoroutineScope(Dispatchers.IO).launch {
            val notificationsEnabled = userPreferencesRepository.notificationsEnabled.first()

            Log.d(TAG, "Notificações habilitadas: $notificationsEnabled")

            if (notificationsEnabled) {
                val soundEnabled = userPreferencesRepository.soundEnabled.first()
                val vibrationEnabled = userPreferencesRepository.vibrationEnabled.first()

                Log.d(TAG, "Som: $soundEnabled, Vibração: $vibrationEnabled")

                NotificationHelper.showBrushingReminder(
                    context = context,
                    notificationId = reminderId,
                    title = title,
                    message = message,
                    enableSound = soundEnabled,
                    enableVibration = vibrationEnabled
                )

                Log.d(TAG, "✅ Notificação disparada!")
            } else {
                Log.w(TAG, "⚠️ Notificações desabilitadas pelo usuário")
            }
        }
    }
}
