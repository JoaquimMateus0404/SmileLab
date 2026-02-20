package com.cleansoft.smilelab.data.local.dao

import androidx.room.*
import com.cleansoft.smilelab.data.local.entity.BrushingReminder
import kotlinx.coroutines.flow.Flow

/**
 * DAO para lembretes de escovação
 */
@Dao
interface BrushingReminderDao {

    @Query("SELECT * FROM brushing_reminders ORDER BY hour ASC, minute ASC")
    fun getAllReminders(): Flow<List<BrushingReminder>>

    @Query("SELECT * FROM brushing_reminders WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    fun getEnabledReminders(): Flow<List<BrushingReminder>>

    @Query("SELECT * FROM brushing_reminders WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    suspend fun getEnabledRemindersList(): List<BrushingReminder>

    @Query("SELECT * FROM brushing_reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): BrushingReminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: BrushingReminder): Long

    @Update
    suspend fun updateReminder(reminder: BrushingReminder)

    @Query("UPDATE brushing_reminders SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setReminderEnabled(id: Long, isEnabled: Boolean)

    @Delete
    suspend fun deleteReminder(reminder: BrushingReminder)

    @Query("DELETE FROM brushing_reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    @Query("DELETE FROM brushing_reminders")
    suspend fun deleteAllReminders()
}
