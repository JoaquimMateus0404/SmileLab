package com.cleansoft.smilelab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade para lembretes de escovação
 * Permite ao utilizador configurar horários de lembretes
 */
@Entity(tableName = "brushing_reminders")
data class BrushingReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val label: String,              // Ex: "Escovação matinal"
    val title: String = "🦷 Hora de Escovar!",
    val message: String? = null,    // Mensagem personalizada (opcional)
    val hour: Int,                  // 0-23
    val minute: Int,                // 0-59
    val isEnabled: Boolean = true,
    val daysOfWeek: List<Int>,      // 1 = Segunda, 7 = Domingo
    val createdAt: Long = System.currentTimeMillis()
)

