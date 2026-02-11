package com.cleansoft.smilelab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cleansoft.smilelab.ui.screens.home.HomeScreen
import com.cleansoft.smilelab.ui.screens.hygiene.BrushingGuideScreen
import com.cleansoft.smilelab.ui.screens.hygiene.FlossingGuideScreen
import com.cleansoft.smilelab.ui.screens.hygiene.OralHygieneScreen
import com.cleansoft.smilelab.ui.screens.hygiene.TongueCleaningGuideScreen
import com.cleansoft.smilelab.ui.screens.knowteeth.KnowYourTeethScreen
import com.cleansoft.smilelab.ui.screens.onboarding.OnboardingScreen
import com.cleansoft.smilelab.ui.screens.problems.DentalProblemsScreen
import com.cleansoft.smilelab.ui.screens.reminders.AddReminderScreen
import com.cleansoft.smilelab.ui.screens.reminders.RemindersScreen
import com.cleansoft.smilelab.ui.screens.routine.RoutineHabitsScreen
import com.cleansoft.smilelab.ui.screens.settings.SettingsScreen
import com.cleansoft.smilelab.ui.screens.splash.SplashScreen
import com.cleansoft.smilelab.ui.screens.viewer3d.Teeth3DViewerScreen
import kotlinx.coroutines.flow.Flow

/**
 * NavGraph principal do SmileLab
 */
@Composable
fun SmileLabNavGraph(
    navController: NavHostController,
    isOnboardingCompleted: Flow<Boolean>,
    onOnboardingComplete: () -> Unit
) {
    val onboardingCompleted by isOnboardingCompleted.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isOnboardingCompleted = onboardingCompleted
            )
        }

        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    onOnboardingComplete()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToKnowTeeth = { navController.navigate(Screen.KnowYourTeeth.route) },
                onNavigateToOralHygiene = { navController.navigate(Screen.OralHygiene.route) },
                onNavigateToDentalProblems = { navController.navigate(Screen.DentalProblems.route) },
                onNavigateToRoutineHabits = { navController.navigate(Screen.RoutineHabits.route) },
                onNavigateTo3DViewer = { navController.navigate(Screen.Teeth3DViewer.route) },
                onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Conhecer os Dentes
        composable(Screen.KnowYourTeeth.route) {
            KnowYourTeethScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateTo3DViewer = { navController.navigate(Screen.Teeth3DViewer.route) }
            )
        }

        // Higiene Bucal
        composable(Screen.OralHygiene.route) {
            OralHygieneScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBrushingGuide = { navController.navigate(Screen.BrushingGuide.route) },
                onNavigateToFlossingGuide = { navController.navigate(Screen.FlossingGuide.route) },
                onNavigateToTongueCleaning = { navController.navigate(Screen.TongueCleaningGuide.route) }
            )
        }

        // Guias de higiene
        composable(Screen.BrushingGuide.route) {
            BrushingGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FlossingGuide.route) {
            FlossingGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TongueCleaningGuide.route) {
            TongueCleaningGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Problemas Dentários
        composable(Screen.DentalProblems.route) {
            DentalProblemsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Rotina & Hábitos
        composable(Screen.RoutineHabits.route) {
            RoutineHabitsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReminders = { navController.navigate(Screen.Reminders.route) }
            )
        }

        // Visualização 3D
        composable(Screen.Teeth3DViewer.route) {
            Teeth3DViewerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Lembretes
        composable(Screen.Reminders.route) {
            RemindersScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddReminder = { navController.navigate(Screen.AddReminder.route) }
            )
        }

        composable(Screen.AddReminder.route) {
            AddReminderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Configurações
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                userPreferencesRepository = userPreferencesRepository
            )
        }
    }
}

