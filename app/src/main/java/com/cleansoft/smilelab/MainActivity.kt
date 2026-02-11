package com.cleansoft.smilelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cleansoft.smilelab.data.repository.UserPreferencesRepository
import com.cleansoft.smilelab.navigation.SmileLabNavGraph
import com.cleansoft.smilelab.ui.theme.SmileLabTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Trocar tema do splash para o tema normal
        setTheme(R.style.Theme_SmileLab)

        // Inicializar repository de preferências
        userPreferencesRepository = UserPreferencesRepository(applicationContext)

        enableEdgeToEdge()
        setContent {
            val darkMode by userPreferencesRepository.darkModeEnabled.collectAsState(initial = false)

            SmileLabTheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val coroutineScope = rememberCoroutineScope()

                    SmileLabNavGraph(
                        navController = navController,
                        isOnboardingCompleted = userPreferencesRepository.isOnboardingCompleted,
                        onOnboardingComplete = {
                            coroutineScope.launch {
                                userPreferencesRepository.setOnboardingCompleted(true)
                            }
                        },
                        userPreferencesRepository = userPreferencesRepository
                    )
                }
            }
        }
    }
}