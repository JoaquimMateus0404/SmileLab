package com.cleansoft.smilelab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade para rastrear progresso do utilizador
 * Guarda quais módulos foram completados e estatísticas
 */
@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val contentId: String,          // ID do conteúdo (ex: "brushing_guide", "know_teeth_types")
    val category: String,           // Categoria (ex: "hygiene", "knowteeth", "problems")
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val viewCount: Int = 0,
    val lastViewedAt: Long = System.currentTimeMillis()
)

