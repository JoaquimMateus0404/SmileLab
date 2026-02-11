package com.cleansoft.smilelab.data.local.converter

import androidx.room.TypeConverter
import com.cleansoft.smilelab.data.local.entity.ContentCategory

/**
 * Type Converters para Room Database
 * Converte tipos complexos para tipos suportados pelo SQLite
 */
class Converters {

    // List<Int> converters (para daysOfWeek)
    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split(",").map { it.toInt() }
        }
    }

    // ContentCategory converters
    @TypeConverter
    fun fromContentCategory(category: ContentCategory): String {
        return category.name
    }

    @TypeConverter
    fun toContentCategory(value: String): ContentCategory {
        return ContentCategory.valueOf(value)
    }
}

