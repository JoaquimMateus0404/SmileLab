package com.cleansoft.smilelab.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

/**
 * Scheduler para agendar lembretes de escovação
 */
class ReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val TAG = "ReminderScheduler"
    }

    /**
     * Agenda um lembrete de escovação
     */
    fun scheduleReminder(
        reminderId: Int,
        hour: Int,
        minute: Int,
        title: String = "🦷 Hora de Escovar!",
        message: String = "Não se esqueça de cuidar do seu sorriso!",
        isRepeating: Boolean = true
    ) {
        Log.d(TAG, "📅 Agendando lembrete #$reminderId para $hour:$minute")

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_MESSAGE, message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Configurar o horário
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Se o horário já passou hoje, agendar para amanhã
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
                Log.d(TAG, "⏭️ Horário já passou hoje, agendando para amanhã")
            }
        }

        val triggerTime = calendar.timeInMillis
        Log.d(TAG, "⏰ Trigger time: ${calendar.time}")

        // Verificar se o app tem permissão para agendar alarmes exatos
        val canScheduleExactAlarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        if (!canScheduleExactAlarms) {
            Log.e(TAG, "❌ Sem permissão para agendar alarmes exatos!")
            return
        }

        try {
            if (isRepeating) {
                // Alarme repetitivo diário
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
                Log.d(TAG, "✅ Lembrete repetitivo agendado com sucesso!")
            } else {
                // Alarme único
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d(TAG, "✅ Lembrete único agendado com sucesso!")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ Erro de permissão ao agendar alarme: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao agendar alarme: ${e.message}")
        }
    }

    /**
     * Cancela um lembrete agendado
     */
    fun cancelReminder(reminderId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    /**
     * Reagenda todos os lembretes ativos
     * Útil após reiniciar o dispositivo
     */
    fun rescheduleAllReminders(reminders: List<ReminderInfo>) {
        reminders.forEach { reminder ->
            scheduleReminder(
                reminderId = reminder.id,
                hour = reminder.hour,
                minute = reminder.minute,
                title = reminder.title,
                message = reminder.message,
                isRepeating = reminder.isRepeating
            )
        }
    }

    data class ReminderInfo(
        val id: Int,
        val hour: Int,
        val minute: Int,
        val title: String,
        val message: String,
        val isRepeating: Boolean
    )
}
