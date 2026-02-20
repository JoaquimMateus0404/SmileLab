package com.cleansoft.smilelab.ui.screens.reminders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cleansoft.smilelab.data.local.SmileLabDatabase
import com.cleansoft.smilelab.data.local.entity.BrushingReminder
import com.cleansoft.smilelab.data.model.AchievementType
import com.cleansoft.smilelab.data.repository.BrushingReminderRepository
import com.cleansoft.smilelab.data.repository.UserPreferencesRepository
import com.cleansoft.smilelab.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar lembretes de escovação
 */
class RemindersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BrushingReminderRepository
    private val scheduler: ReminderScheduler
    private val preferencesRepository: UserPreferencesRepository

    val allReminders: Flow<List<BrushingReminder>>

    init {
        val dao = SmileLabDatabase.getDatabase(application).brushingReminderDao()
        repository = BrushingReminderRepository(dao)
        scheduler = ReminderScheduler(application)
        preferencesRepository = UserPreferencesRepository(application)
        allReminders = repository.allReminders
    }

    /**
     * Adiciona um novo lembrete e agenda a notificação
     */
    fun addReminder(
        label: String,
        hour: Int,
        minute: Int,
        daysOfWeek: List<Int>,
        title: String = "🦷 Hora de Escovar!",
        message: String? = null
    ) = viewModelScope.launch {
        val reminder = BrushingReminder(
            label = label,
            title = title,
            message = message,
            hour = hour,
            minute = minute,
            isEnabled = true,
            daysOfWeek = daysOfWeek
        )

        preferencesRepository.unlockAchievement(AchievementType.FIRST_REMINDER.name)

        val reminderId = repository.insertReminder(reminder)

        // Agendar notificação
        scheduler.scheduleReminder(
            reminderId = reminderId.toInt(),
            hour = hour,
            minute = minute,
            title = title,
            message = message ?: "Não se esqueça de cuidar do seu sorriso!",
            isRepeating = true,
            daysOfWeek = daysOfWeek
        )
    }

    /**
     * Atualiza um lembrete existente
     */
    fun updateReminder(reminder: BrushingReminder) = viewModelScope.launch {
        repository.updateReminder(reminder)

        if (reminder.isEnabled) {
            // Reagendar notificação
            scheduler.scheduleReminder(
                reminderId = reminder.id.toInt(),
                hour = reminder.hour,
                minute = reminder.minute,
                title = reminder.title,
                message = reminder.message ?: "Não se esqueça de cuidar do seu sorriso!",
                isRepeating = true,
                daysOfWeek = reminder.daysOfWeek
            )
        } else {
            // Cancelar notificação
            scheduler.cancelReminder(reminder.id.toInt())
        }
    }

    /**
     * Ativa ou desativa um lembrete
     */
    fun toggleReminder(id: Long, isEnabled: Boolean) = viewModelScope.launch {
        repository.setReminderEnabled(id, isEnabled)

        if (!isEnabled) {
            scheduler.cancelReminder(id.toInt())
        } else {
            // Precisa buscar o reminder para reagendar
            val reminder = repository.getReminderById(id)
            reminder?.let {
                scheduler.scheduleReminder(
                    reminderId = it.id.toInt(),
                    hour = it.hour,
                    minute = it.minute,
                    title = it.title,
                    message = it.message ?: "Não se esqueça de cuidar do seu sorriso!",
                    isRepeating = true,
                    daysOfWeek = it.daysOfWeek
                )
            }
        }
    }

    /**
     * Deleta um lembrete e cancela a notificação
     */
    fun deleteReminder(reminder: BrushingReminder) = viewModelScope.launch {
        repository.deleteReminder(reminder)
        scheduler.cancelReminder(reminder.id.toInt())
    }
}

