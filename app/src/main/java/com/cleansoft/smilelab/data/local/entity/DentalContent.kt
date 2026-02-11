package com.cleansoft.smilelab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade para conteúdo educativo dental
 * Representa artigos, guias e informações sobre dentes e higiene
 */
@Entity(tableName = "dental_content")
data class DentalContent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val description: String,
    val content: String,
    val category: ContentCategory,
    val imageResName: String? = null,
    val model3DPath: String? = null,
    val order: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Categorias de conteúdo
 */
enum class ContentCategory {
    CONHECER_DENTES,      // Anatomia dental
    HIGIENE_BUCAL,        // Técnicas de higiene
    PROBLEMAS_DENTARIOS,  // Cáries, gengivite, etc.
    ROTINA_HABITOS        // Hábitos saudáveis
}

