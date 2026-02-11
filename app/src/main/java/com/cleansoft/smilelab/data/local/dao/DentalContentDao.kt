package com.cleansoft.smilelab.data.local.dao

import androidx.room.*
import com.cleansoft.smilelab.data.local.entity.ContentCategory
import com.cleansoft.smilelab.data.local.entity.DentalContent
import kotlinx.coroutines.flow.Flow

/**
 * DAO para conteúdo educativo dental
 */
@Dao
interface DentalContentDao {

    @Query("SELECT * FROM dental_content ORDER BY `order` ASC")
    fun getAllContent(): Flow<List<DentalContent>>

    @Query("SELECT * FROM dental_content WHERE category = :category ORDER BY `order` ASC")
    fun getContentByCategory(category: ContentCategory): Flow<List<DentalContent>>

    @Query("SELECT * FROM dental_content WHERE id = :id")
    suspend fun getContentById(id: Long): DentalContent?

    @Query("SELECT * FROM dental_content WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchContent(query: String): Flow<List<DentalContent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: DentalContent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContent(contents: List<DentalContent>)

    @Update
    suspend fun updateContent(content: DentalContent)

    @Delete
    suspend fun deleteContent(content: DentalContent)

    @Query("DELETE FROM dental_content")
    suspend fun deleteAllContent()

    @Query("SELECT COUNT(*) FROM dental_content")
    suspend fun getContentCount(): Int
}

