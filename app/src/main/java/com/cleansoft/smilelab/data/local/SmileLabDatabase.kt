package com.cleansoft.smilelab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cleansoft.smilelab.data.local.dao.BrushingReminderDao
import com.cleansoft.smilelab.data.local.dao.DentalContentDao
import com.cleansoft.smilelab.data.local.dao.UserProgressDao
import com.cleansoft.smilelab.data.local.entity.BrushingReminder
import com.cleansoft.smilelab.data.local.entity.DentalContent
import com.cleansoft.smilelab.data.local.entity.UserProgress
import com.cleansoft.smilelab.data.local.converter.Converters

/**
 * Room Database para SmileLab
 * Armazena todo o conteúdo offline-first
 */
@Database(
    entities = [
        DentalContent::class,
        UserProgress::class,
        BrushingReminder::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SmileLabDatabase : RoomDatabase() {

    abstract fun dentalContentDao(): DentalContentDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun brushingReminderDao(): BrushingReminderDao

    companion object {
        @Volatile
        private var INSTANCE: SmileLabDatabase? = null

        fun getDatabase(context: Context): SmileLabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmileLabDatabase::class.java,
                    "smilelab_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

