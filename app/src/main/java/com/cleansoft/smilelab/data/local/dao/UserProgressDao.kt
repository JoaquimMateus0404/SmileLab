package com.cleansoft.smilelab.data.local.dao

import androidx.room.*
import com.cleansoft.smilelab.data.local.entity.UserProgress
import kotlinx.coroutines.flow.Flow

/**
 * DAO para progresso do utilizador
 */
@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress ORDER BY lastViewedAt DESC")
    fun getAllProgress(): Flow<List<UserProgress>>

    @Query("SELECT * FROM user_progress ORDER BY lastViewedAt DESC")
    suspend fun getAllProgressList(): List<UserProgress>

    @Query("SELECT * FROM user_progress WHERE contentId = :contentId")
    suspend fun getProgressByContentId(contentId: String): UserProgress?

    @Query("SELECT * FROM user_progress WHERE category = :category")
    fun getProgressByCategory(category: String): Flow<List<UserProgress>>

    @Query("SELECT * FROM user_progress WHERE isCompleted = 1")
    fun getCompletedContent(): Flow<List<UserProgress>>

    @Query("SELECT COUNT(*) FROM user_progress WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgress): Long

    @Update
    suspend fun updateProgress(progress: UserProgress)

    @Query("UPDATE user_progress SET isCompleted = 1, completedAt = :completedAt WHERE contentId = :contentId")
    suspend fun markAsCompleted(contentId: String, completedAt: Long)

    @Query("UPDATE user_progress SET viewCount = viewCount + 1 WHERE contentId = :contentId")
    suspend fun incrementViewCount(contentId: String)

    @Query("UPDATE user_progress SET lastViewedAt = :timestamp WHERE contentId = :contentId")
    suspend fun updateLastViewed(contentId: String, timestamp: Long)

    @Delete
    suspend fun deleteProgress(progress: UserProgress)

    @Query("DELETE FROM user_progress WHERE category = :category")
    suspend fun deleteProgressByCategory(category: String)

    @Query("DELETE FROM user_progress")
    suspend fun deleteAllProgress()
}

