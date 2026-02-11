package com.cleansoft.smilelab.data.repository

import com.cleansoft.smilelab.data.local.dao.BrushingReminderDao
import com.cleansoft.smilelab.data.local.entity.BrushingReminder
import kotlinx.coroutines.flow.Flow

/**
 * Repository para lembretes de escovação
 */
class BrushingReminderRepository(
    private val brushingReminderDao: BrushingReminderDao
) {

    val allReminders: Flow<List<BrushingReminder>> = brushingReminderDao.getAllReminders()
    val enabledReminders: Flow<List<BrushingReminder>> = brushingReminderDao.getEnabledReminders()

    suspend fun getReminderById(id: Long): BrushingReminder? {
        return brushingReminderDao.getReminderById(id)
    }

    suspend fun insertReminder(reminder: BrushingReminder): Long {
        return brushingReminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: BrushingReminder) {
        brushingReminderDao.updateReminder(reminder)
    }

    suspend fun setReminderEnabled(id: Long, isEnabled: Boolean) {
        brushingReminderDao.setReminderEnabled(id, isEnabled)
    }

    suspend fun deleteReminder(reminder: BrushingReminder) {
        brushingReminderDao.deleteReminder(reminder)
    }

    suspend fun deleteReminderById(id: Long) {
        brushingReminderDao.deleteReminderById(id)
    }
}

