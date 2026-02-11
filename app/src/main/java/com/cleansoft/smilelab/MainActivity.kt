package com.cleansoft.smilelab

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.cleansoft.smilelab.data.repository.UserPreferencesRepository
import com.cleansoft.smilelab.navigation.SmileLabNavGraph
import com.cleansoft.smilelab.notifications.PermissionHelper
import com.cleansoft.smilelab.ui.theme.SmileLabTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository

    // Launcher para solicitar permissão de notificações
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "✅ Permissão de notificações concedida")
        } else {
            Log.w("MainActivity", "⚠️ Permissão de notificações negada")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Trocar tema do splash para o tema normal
        setTheme(R.style.Theme_SmileLab)

        // Inicializar repository de preferências
        userPreferencesRepository = UserPreferencesRepository(applicationContext)

        // Solicitar permissões de notificação
        requestNotificationPermissions()

        // Verificar permissão de alarmes exatos
        checkAlarmPermission()

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

    private fun requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "✅ Permissão de notificações já concedida")
                }
                else -> {
                    Log.d("MainActivity", "📢 Solicitando permissão de notificações")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun checkAlarmPermission() {
        if (!PermissionHelper.canScheduleExactAlarms(this)) {
            Log.w("MainActivity", "⚠️ Permissão de alarmes exatos não concedida")
        } else {
            Log.d("MainActivity", "✅ Permissão de alarmes exatos concedida")
        }
    }
}