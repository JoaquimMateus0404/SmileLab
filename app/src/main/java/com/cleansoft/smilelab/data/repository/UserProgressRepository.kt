package com.cleansoft.smilelab.data.repository

import com.cleansoft.smilelab.data.local.dao.UserProgressDao
import com.cleansoft.smilelab.data.local.entity.UserProgress
import kotlinx.coroutines.flow.Flow

/**
 * Repository para progresso do utilizador
 */
class UserProgressRepository(
    private val userProgressDao: UserProgressDao
) {

    /**
     * Obtém todo o progresso do utilizador
     */
    fun getAllProgress(): Flow<List<UserProgress>> {
        return userProgressDao.getAllProgress()
    }

    /**
     * Obtém progresso de um conteúdo específico
     */
    suspend fun getProgressByContentId(contentId: String): UserProgress? {
        return userProgressDao.getProgressByContentId(contentId)
    }

    /**
     * Obtém progresso por categoria
     */
    fun getProgressByCategory(category: String): Flow<List<UserProgress>> {
        return userProgressDao.getProgressByCategory(category)
    }

    /**
     * Obtém conteúdos completados
     */
    fun getCompletedContent(): Flow<List<UserProgress>> {
        return userProgressDao.getCompletedContent()
    }

    /**
     * Marca conteúdo como visualizado
     */
    suspend fun markContentAsViewed(contentId: String, category: String) {
        val existing = userProgressDao.getProgressByContentId(contentId)

        if (existing == null) {
            // Criar novo progresso
            val progress = UserProgress(
                contentId = contentId,
                category = category,
                viewCount = 1,
                lastViewedAt = System.currentTimeMillis()
            )
            userProgressDao.insertProgress(progress)
        } else {
            // Atualizar contagem de visualizações
            userProgressDao.incrementViewCount(contentId)
            userProgressDao.updateLastViewed(contentId, System.currentTimeMillis())
        }
    }

    /**
     * Marca conteúdo como completado
     */
    suspend fun markContentAsCompleted(contentId: String, category: String) {
        val existing = userProgressDao.getProgressByContentId(contentId)

        if (existing == null) {
            // Criar novo progresso já completado
            val progress = UserProgress(
                contentId = contentId,
                category = category,
                viewCount = 1,
                isCompleted = true,
                completedAt = System.currentTimeMillis(),
                lastViewedAt = System.currentTimeMillis()
            )
            userProgressDao.insertProgress(progress)
        } else {
            // Atualizar como completado
            userProgressDao.markAsCompleted(contentId, System.currentTimeMillis())
            userProgressDao.incrementViewCount(contentId)
            userProgressDao.updateLastViewed(contentId, System.currentTimeMillis())
        }
    }

    /**
     * Obtém estatísticas gerais de progresso
     */
    suspend fun getProgressStats(): ProgressStats {
        val allProgress = userProgressDao.getAllProgressList()
        val completed = allProgress.count { it.isCompleted }
        val total = allProgress.size
        val percentage = if (total > 0) (completed.toFloat() / total.toFloat() * 100).toInt() else 0

        val categoriesProgress = allProgress.groupBy { it.category }.mapValues { entry ->
            val categoryCompleted = entry.value.count { it.isCompleted }
            val categoryTotal = entry.value.size
            categoryCompleted to categoryTotal
        }

        return ProgressStats(
            totalViewed = total,
            totalCompleted = completed,
            completionPercentage = percentage,
            categoriesProgress = categoriesProgress
        )
    }

    /**
     * Reseta todo o progresso
     */
    suspend fun resetAllProgress() {
        userProgressDao.deleteAllProgress()
    }

    /**
     * Reseta progresso de uma categoria
     */
    suspend fun resetCategoryProgress(category: String) {
        userProgressDao.deleteProgressByCategory(category)
    }
}

/**
 * Estatísticas de progresso do utilizador
 */
data class ProgressStats(
    val totalViewed: Int,
    val totalCompleted: Int,
    val completionPercentage: Int,
    val categoriesProgress: Map<String, Pair<Int, Int>> // categoria -> (completado, total)
)

