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
 *
 * Estratégia:
 * - Usa alarme EXATO + ALLOW_WHILE_IDLE para reduzir atrasos.
 * - Para lembretes recorrentes, agenda sempre só a PRÓXIMA ocorrência.
 *   Quando o receiver dispara, ele agenda a seguinte.
 */
class ReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val TAG = "ReminderScheduler"

        const val EXTRA_IS_REPEATING = "is_repeating"
        const val EXTRA_HOUR = "hour"
        const val EXTRA_MINUTE = "minute"
        const val EXTRA_DAYS_OF_WEEK = "days_of_week"
    }

    fun scheduleReminder(
        reminderId: Int,
        hour: Int,
        minute: Int,
        daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
        title: String = "🦷 Hora de Escovar!",
        message: String = "Não se esqueça de cuidar do seu sorriso!",
        isRepeating: Boolean = true
    ) {
        Log.d(TAG, "📅 Agendando lembrete #$reminderId para $hour:$minute")

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.cleansoft.smilelab.REMINDER_$reminderId"
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_MESSAGE, message)
            putExtra(EXTRA_IS_REPEATING, isRepeating)
            putExtra(EXTRA_HOUR, hour)
            putExtra(EXTRA_MINUTE, minute)
            putExtra(EXTRA_DAYS_OF_WEEK, daysOfWeek.distinct().sorted().joinToString(","))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val canScheduleExactAlarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        if (!canScheduleExactAlarms) {
            Log.e(TAG, "❌ Sem permissão para agendar alarmes exatos!")
            return
        }

        val triggerTime = computeNextTriggerTime(hour, minute, daysOfWeek)
        Log.d(TAG, "⏰ Próximo disparo: ${Calendar.getInstance().apply { timeInMillis = triggerTime }.time}")

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            Log.d(TAG, "✅ Lembrete exato agendado com sucesso")
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ Erro de permissão ao agendar alarme: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao agendar alarme: ${e.message}")
        }
    }

    fun cancelReminder(reminderId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.cleansoft.smilelab.REMINDER_$reminderId"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            Log.d(TAG, "🛑 Lembrete #$reminderId cancelado")
        }
    }

    fun rescheduleAllReminders(reminders: List<ReminderInfo>) {
        reminders.forEach { reminder ->
            scheduleReminder(
                reminderId = reminder.id,
                hour = reminder.hour,
                minute = reminder.minute,
                daysOfWeek = reminder.daysOfWeek,
                title = reminder.title,
                message = reminder.message,
                isRepeating = reminder.isRepeating
            )
        }
    }

    private fun computeNextTriggerTime(hour: Int, minute: Int, daysOfWeek: List<Int>): Long {
        val validDays = if (daysOfWeek.isEmpty()) {
            listOf(1, 2, 3, 4, 5, 6, 7)
        } else {
            daysOfWeek.filter { it in 1..7 }.distinct().sorted()
        }

        val now = Calendar.getInstance()
        val base = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        for (offset in 0..7) {
            val candidate = (base.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, offset)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }

            val day = toAppDayOfWeek(candidate.get(Calendar.DAY_OF_WEEK))
            if (day in validDays && candidate.timeInMillis > now.timeInMillis) {
                return candidate.timeInMillis
            }
        }

        // fallback: amanhã no horário informado
        return (base.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.timeInMillis
    }

    private fun toAppDayOfWeek(calendarDay: Int): Int {
        // Calendar: Sunday=1 ... Saturday=7
        // App: Monday=1 ... Sunday=7
        return if (calendarDay == Calendar.SUNDAY) 7 else calendarDay - 1
    }

    data class ReminderInfo(
        val id: Int,
        val hour: Int,
        val minute: Int,
        val daysOfWeek: List<Int>,
        val title: String,
        val message: String,
        val isRepeating: Boolean
    )
}
