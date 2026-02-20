package com.cleansoft.smilelab.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cleansoft.smilelab.data.model.AchievementType
import com.cleansoft.smilelab.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

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

        val isRepeating = intent.getBooleanExtra(ReminderScheduler.EXTRA_IS_REPEATING, true)
        val hour = intent.getIntExtra(ReminderScheduler.EXTRA_HOUR, 8)
        val minute = intent.getIntExtra(ReminderScheduler.EXTRA_MINUTE, 0)
        val daysOfWeek = intent
            .getStringExtra(ReminderScheduler.EXTRA_DAYS_OF_WEEK)
            .orEmpty()
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .filter { it in 1..7 }

        Log.d(TAG, "🔔 Recebido lembrete #$reminderId")

        if (reminderId == -1) {
            Log.e(TAG, "❌ ID de lembrete inválido!")
            return
        }

        val userPreferencesRepository = UserPreferencesRepository(context)

        CoroutineScope(Dispatchers.IO).launch {
            val notificationsEnabled = userPreferencesRepository.notificationsEnabled.first()
            Log.d(TAG, "Notificações habilitadas: $notificationsEnabled")

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

                Log.d(TAG, "✅ Notificação disparada!")
            } else {
                Log.w(TAG, "⚠️ Notificações desabilitadas pelo usuário")
            }

            // Progresso gamificado do utilizador
            userPreferencesRepository.recordBrushingEvent()
            userPreferencesRepository.unlockAchievement(AchievementType.FIRST_REMINDER.name)

            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (currentHour < 8) {
                userPreferencesRepository.unlockAchievement(AchievementType.EARLY_BIRD.name)
            }
            if (currentHour >= 22) {
                userPreferencesRepository.unlockAchievement(AchievementType.NIGHT_OWL.name)
            }

            // Para recorrência precisa: sempre reagenda a próxima ocorrência exata
            if (isRepeating) {
                ReminderScheduler(context).scheduleReminder(
                    reminderId = reminderId,
                    hour = hour,
                    minute = minute,
                    daysOfWeek = daysOfWeek,
                    title = title,
                    message = message,
                    isRepeating = true
                )
            }
        }
    }
}
