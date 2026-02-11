package com.cleansoft.smilelab.navigation

/**
 * Rotas de navegação do SmileLab
 */
sealed class Screen(val route: String) {

    // Splash & Onboarding
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")

    // Main screens
    data object Home : Screen("home")

    // Módulos educativos
    data object KnowYourTeeth : Screen("know_your_teeth")
    data object OralHygiene : Screen("oral_hygiene")
    data object DentalProblems : Screen("dental_problems")
    data object RoutineHabits : Screen("routine_habits")

    // Visualização 3D
    data object Teeth3DViewer : Screen("teeth_3d_viewer")
    data object ToothDetail : Screen("tooth_detail/{toothId}") {
        fun createRoute(toothId: Int) = "tooth_detail/$toothId"
    }

    // Guias de higiene
    data object BrushingGuide : Screen("brushing_guide")
    data object FlossingGuide : Screen("flossing_guide")
    data object TongueCleaningGuide : Screen("tongue_cleaning_guide")

    // Lembretes
    data object Reminders : Screen("reminders")
    data object AddReminder : Screen("add_reminder")
    data object EditReminder : Screen("edit_reminder/{reminderId}") {
        fun createRoute(reminderId: Long) = "edit_reminder/$reminderId"
    }

    // Conteúdo detalhe
    data object ContentDetail : Screen("content_detail/{contentId}") {
        fun createRoute(contentId: Long) = "content_detail/$contentId"
    }

    // Configurações
    data object Settings : Screen("settings")
}

