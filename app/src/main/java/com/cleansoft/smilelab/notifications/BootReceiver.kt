package com.cleansoft.smilelab.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cleansoft.smilelab.data.local.SmileLabDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver para reagendar lembretes após reiniciar o dispositivo
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reagendar todos os lembretes ativos
            CoroutineScope(Dispatchers.IO).launch {
                val database = SmileLabDatabase.getDatabase(context)
                database.brushingReminderDao().getAllReminders().collect { reminders ->
                    val scheduler = ReminderScheduler(context)
                    val reminderInfoList = reminders
                        .filter { it.isEnabled }
                        .map { reminder ->
                            ReminderScheduler.ReminderInfo(
                                id = reminder.id.toInt(),
                                hour = reminder.hour,
                                minute = reminder.minute,
                                title = reminder.title,
                                message = reminder.message ?: "Não se esqueça de cuidar do seu sorriso!",
                                isRepeating = true
                            )
                        }

                    scheduler.rescheduleAllReminders(reminderInfoList)
                }
            }
        }
    }
}

