package com.cleansoft.smilelab.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Calendar

// Extension property para DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smilelab_preferences")

/**
 * Repository para preferências do utilizador usando DataStore
 */
class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_NAME = stringPreferencesKey("user_name")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val FIRST_LAUNCH_TIMESTAMP = longPreferencesKey("first_launch_timestamp")

        // Gamificação / progresso real
        val TOTAL_BRUSHING_EVENTS = intPreferencesKey("total_brushing_events")
        val CURRENT_STREAK_DAYS = intPreferencesKey("current_streak_days")
        val BEST_STREAK_DAYS = intPreferencesKey("best_streak_days")
        val LAST_BRUSHING_DAY_KEY = intPreferencesKey("last_brushing_day_key")
        val UNLOCKED_ACHIEVEMENTS = stringSetPreferencesKey("unlocked_achievements")
    }

    // Flow para verificar se onboarding foi completado
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    // Flow para nome do utilizador
    val userName: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: ""
        }

    // Flow para notificações ativadas
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    // Flow para modo escuro
    val darkModeEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false
        }

    // Flow para som nas notificações
    val soundEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] ?: true
        }

    // Flow para vibração nas notificações
    val vibrationEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true
        }

    val unlockedAchievements: Flow<Set<String>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[PreferencesKeys.UNLOCKED_ACHIEVEMENTS] ?: emptySet() }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setFirstLaunchTimestamp() {
        context.dataStore.edit { preferences ->
            if (preferences[PreferencesKeys.FIRST_LAUNCH_TIMESTAMP] == null) {
                preferences[PreferencesKeys.FIRST_LAUNCH_TIMESTAMP] = System.currentTimeMillis()
            }
        }
    }

    suspend fun unlockAchievement(achievementTypeName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.UNLOCKED_ACHIEVEMENTS] ?: emptySet()
            preferences[PreferencesKeys.UNLOCKED_ACHIEVEMENTS] = current + achievementTypeName
        }
    }

    suspend fun recordBrushingEvent(timestamp: Long = System.currentTimeMillis()) {
        context.dataStore.edit { preferences ->
            val todayKey = dayKey(timestamp)
            val lastDayKey = preferences[PreferencesKeys.LAST_BRUSHING_DAY_KEY]

            val currentStreak = preferences[PreferencesKeys.CURRENT_STREAK_DAYS] ?: 0
            val newStreak = when {
                lastDayKey == null -> 1
                lastDayKey == todayKey -> currentStreak // mesmo dia, não aumenta streak
                todayKey == lastDayKey + 1 -> currentStreak + 1
                else -> 1
            }

            preferences[PreferencesKeys.CURRENT_STREAK_DAYS] = newStreak
            val best = preferences[PreferencesKeys.BEST_STREAK_DAYS] ?: 0
            preferences[PreferencesKeys.BEST_STREAK_DAYS] = maxOf(best, newStreak)
            preferences[PreferencesKeys.LAST_BRUSHING_DAY_KEY] = todayKey

            val currentTotal = preferences[PreferencesKeys.TOTAL_BRUSHING_EVENTS] ?: 0
            preferences[PreferencesKeys.TOTAL_BRUSHING_EVENTS] = currentTotal + 1
        }
    }

    suspend fun getGamificationSnapshot(): GamificationSnapshot {
        val preferences = context.dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .first()

        return GamificationSnapshot(
            totalBrushingEvents = preferences[PreferencesKeys.TOTAL_BRUSHING_EVENTS] ?: 0,
            currentStreakDays = preferences[PreferencesKeys.CURRENT_STREAK_DAYS] ?: 0,
            bestStreakDays = preferences[PreferencesKeys.BEST_STREAK_DAYS] ?: 0,
            unlockedAchievements = preferences[PreferencesKeys.UNLOCKED_ACHIEVEMENTS] ?: emptySet()
        )
    }

    private fun dayKey(timestamp: Long): Int {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val year = calendar.get(Calendar.YEAR)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        return year * 1000 + dayOfYear
    }
}

data class GamificationSnapshot(
    val totalBrushingEvents: Int = 0,
    val currentStreakDays: Int = 0,
    val bestStreakDays: Int = 0,
    val unlockedAchievements: Set<String> = emptySet()
)
