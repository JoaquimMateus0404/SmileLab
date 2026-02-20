package com.cleansoft.smilelab.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cleansoft.smilelab.data.local.SmileLabDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver para reagendar lembretes após reiniciar o dispositivo
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {
            val database = SmileLabDatabase.getDatabase(context)
            val reminders = database.brushingReminderDao().getEnabledRemindersList()
            val scheduler = ReminderScheduler(context)

            val reminderInfoList = reminders.map { reminder ->
                ReminderScheduler.ReminderInfo(
                    id = reminder.id.toInt(),
                    hour = reminder.hour,
                    minute = reminder.minute,
                    daysOfWeek = reminder.daysOfWeek,
                    title = reminder.title,
                    message = reminder.message ?: "Não se esqueça de cuidar do seu sorriso!",
                    isRepeating = true
                )
            }

            scheduler.rescheduleAllReminders(reminderInfoList)
            Log.d(TAG, "✅ Reagendados ${reminderInfoList.size} lembretes após boot")
        }
    }
}
