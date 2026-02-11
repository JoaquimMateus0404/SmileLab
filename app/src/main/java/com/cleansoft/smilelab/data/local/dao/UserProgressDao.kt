package com.cleansoft.smilelab.data.local.dao

import androidx.room.*
import com.cleansoft.smilelab.data.local.entity.UserProgress
import kotlinx.coroutines.flow.Flow

/**
 * DAO para progresso do utilizador
 */
@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress")
    fun getAllProgress(): Flow<List<UserProgress>>

    @Query("SELECT * FROM user_progress WHERE contentId = :contentId")
    suspend fun getProgressByContentId(contentId: Long): UserProgress?

    @Query("SELECT * FROM user_progress WHERE isCompleted = 1")
    fun getCompletedProgress(): Flow<List<UserProgress>>

    @Query("SELECT COUNT(*) FROM user_progress WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgress): Long

    @Update
    suspend fun updateProgress(progress: UserProgress)

    @Query("UPDATE user_progress SET isCompleted = 1, completedAt = :completedAt WHERE contentId = :contentId")
    suspend fun markAsCompleted(contentId: Long, completedAt: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET viewCount = viewCount + 1, lastViewedAt = :timestamp WHERE contentId = :contentId")
    suspend fun incrementViewCount(contentId: Long, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteProgress(progress: UserProgress)

    @Query("DELETE FROM user_progress")
    suspend fun deleteAllProgress()
}

