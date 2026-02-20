package com.cleansoft.smilelab.data.repository

import com.cleansoft.smilelab.data.local.dao.BrushingReminderDao
import com.cleansoft.smilelab.data.local.dao.UserProgressDao
import com.cleansoft.smilelab.data.model.Achievement
import com.cleansoft.smilelab.data.model.AchievementType
import com.cleansoft.smilelab.data.model.Achievements

class AchievementsRepository(
    private val userProgressDao: UserProgressDao,
    private val brushingReminderDao: BrushingReminderDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend fun getAchievements(): List<Achievement> {
        val allProgress = userProgressDao.getAllProgressList()
        val gamification = userPreferencesRepository.getGamificationSnapshot()
        val reminders = brushingReminderDao.getEnabledRemindersList()

        val viewedContent = allProgress.size
        val completedCategories = allProgress
            .groupBy { it.category }
            .count { (_, items) -> items.isNotEmpty() && items.all { it.isCompleted } }

        val unlockedSet = gamification.unlockedAchievements

        return Achievements.getAll().map { base ->
            when (base.type) {
                AchievementType.FIRST_REMINDER -> base.copy(
                    isUnlocked = reminders.isNotEmpty() || unlockedSet.contains(base.type.name),
                    progress = if (reminders.isNotEmpty()) 1 else 0,
                    maxProgress = 1
                )

                AchievementType.WEEK_STREAK -> base.copy(
                    progress = gamification.currentStreakDays.coerceAtMost(base.maxProgress),
                    isUnlocked = gamification.currentStreakDays >= 7 || unlockedSet.contains(base.type.name)
                )

                AchievementType.MONTH_STREAK -> base.copy(
                    progress = gamification.currentStreakDays.coerceAtMost(base.maxProgress),
                    isUnlocked = gamification.currentStreakDays >= 30 || unlockedSet.contains(base.type.name)
                )

                AchievementType.EARLY_BIRD,
                AchievementType.NIGHT_OWL,
                AchievementType.QUIZ_MASTER -> base.copy(
                    progress = if (unlockedSet.contains(base.type.name)) 1 else 0,
                    maxProgress = 1,
                    isUnlocked = unlockedSet.contains(base.type.name)
                )

                AchievementType.CONTENT_EXPLORER -> base.copy(
                    progress = viewedContent.coerceAtMost(base.maxProgress),
                    isUnlocked = viewedContent >= base.maxProgress
                )

                AchievementType.DENTAL_EXPERT -> base.copy(
                    progress = completedCategories.coerceAtMost(base.maxProgress),
                    isUnlocked = completedCategories >= base.maxProgress
                )

                AchievementType.CONSISTENT_BRUSHER -> base.copy(
                    progress = gamification.totalBrushingEvents.coerceAtMost(base.maxProgress),
                    isUnlocked = gamification.totalBrushingEvents >= base.maxProgress
                )

                AchievementType.PERFECT_WEEK -> {
                    val progress = minOf(gamification.currentStreakDays * 3, base.maxProgress)
                    base.copy(
                        progress = progress,
                        isUnlocked = progress >= base.maxProgress
                    )
                }
            }
        }
    }
}
