package com.cleansoft.smilelab.data.repository

import com.cleansoft.smilelab.data.local.dao.DentalContentDao
import com.cleansoft.smilelab.data.local.entity.ContentCategory
import com.cleansoft.smilelab.data.local.entity.DentalContent
import kotlinx.coroutines.flow.Flow

/**
 * Repository para conteúdo educativo dental
 * Camada de abstração entre ViewModel e fonte de dados
 */
class DentalContentRepository(
    private val dentalContentDao: DentalContentDao
) {

    val allContent: Flow<List<DentalContent>> = dentalContentDao.getAllContent()

    fun getContentByCategory(category: ContentCategory): Flow<List<DentalContent>> {
        return dentalContentDao.getContentByCategory(category)
    }

    suspend fun getContentById(id: Long): DentalContent? {
        return dentalContentDao.getContentById(id)
    }

    fun searchContent(query: String): Flow<List<DentalContent>> {
        return dentalContentDao.searchContent(query)
    }

    suspend fun insertContent(content: DentalContent): Long {
        return dentalContentDao.insertContent(content)
    }

    suspend fun insertAllContent(contents: List<DentalContent>) {
        dentalContentDao.insertAllContent(contents)
    }

    suspend fun updateContent(content: DentalContent) {
        dentalContentDao.updateContent(content)
    }

    suspend fun deleteContent(content: DentalContent) {
        dentalContentDao.deleteContent(content)
    }

    suspend fun getContentCount(): Int {
        return dentalContentDao.getContentCount()
    }
}

