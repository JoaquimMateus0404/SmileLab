package com.cleansoft.smilelab.data.model

/**
 * Tipos de conquistas disponíveis
 */
enum class AchievementType {
    FIRST_REMINDER,           // Primeiro lembrete criado
    WEEK_STREAK,              // 7 dias consecutivos
    MONTH_STREAK,             // 30 dias consecutivos
    EARLY_BIRD,               // Escovou antes das 8h
    NIGHT_OWL,                // Escovou depois das 22h
    CONTENT_EXPLORER,         // Visualizou todos os conteúdos
    QUIZ_MASTER,              // Acertou 100% em um quiz
    DENTAL_EXPERT,            // Completou todos os módulos
    CONSISTENT_BRUSHER,       // 100 escovações registradas
    PERFECT_WEEK             // Escovou 3x por dia durante 7 dias
}

/**
 * Modelo de conquista
 */
data class Achievement(
    val type: AchievementType,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val maxProgress: Int = 1
) {
    val progressPercentage: Int
        get() = if (maxProgress > 0) (progress * 100) / maxProgress else 0
}

/**
 * Conquistas disponíveis no app
 */
object Achievements {
    fun getAll() = listOf(
        Achievement(
            type = AchievementType.FIRST_REMINDER,
            title = "Primeiro Passo",
            description = "Criou seu primeiro lembrete",
            icon = "🎯"
        ),
        Achievement(
            type = AchievementType.WEEK_STREAK,
            title = "Semana Perfeita",
            description = "7 dias seguidos de escovação",
            icon = "📅",
            maxProgress = 7
        ),
        Achievement(
            type = AchievementType.MONTH_STREAK,
            title = "Mestre da Consistência",
            description = "30 dias seguidos de escovação",
            icon = "🏆",
            maxProgress = 30
        ),
        Achievement(
            type = AchievementType.EARLY_BIRD,
            title = "Madrugador",
            description = "Escovou antes das 8h da manhã",
            icon = "🌅"
        ),
        Achievement(
            type = AchievementType.NIGHT_OWL,
            title = "Coruja Noturna",
            description = "Escovou depois das 22h",
            icon = "🦉"
        ),
        Achievement(
            type = AchievementType.CONTENT_EXPLORER,
            title = "Explorador",
            description = "Visualizou todo o conteúdo educativo",
            icon = "🗺️",
            maxProgress = 15
        ),
        Achievement(
            type = AchievementType.QUIZ_MASTER,
            title = "Mestre dos Quizzes",
            description = "Acertou 100% das questões",
            icon = "🎓"
        ),
        Achievement(
            type = AchievementType.DENTAL_EXPERT,
            title = "Expert Dental",
            description = "Completou todos os módulos",
            icon = "⭐",
            maxProgress = 4
        ),
        Achievement(
            type = AchievementType.CONSISTENT_BRUSHER,
            title = "Escovador Consistente",
            description = "Registrou 100 escovações",
            icon = "💯",
            maxProgress = 100
        ),
        Achievement(
            type = AchievementType.PERFECT_WEEK,
            title = "Semana Impecável",
            description = "Escovou 3x por dia durante 7 dias",
            icon = "✨",
            maxProgress = 21
        )
    )
}

