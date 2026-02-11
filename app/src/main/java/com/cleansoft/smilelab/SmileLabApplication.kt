package com.cleansoft.smilelab

import android.app.Application
import android.util.Log
import com.cleansoft.smilelab.data.local.SmileLabDatabase
import com.cleansoft.smilelab.filament.FilamentEngineManager

/**
 * SmileLab Application class
 * Inicializa componentes globais do app
 */
class SmileLabApplication : Application() {

    companion object {
        private const val TAG = "SmileLabApp"
        lateinit var instance: SmileLabApplication
            private set
    }

    // Lazy initialization do database
    val database: SmileLabDatabase by lazy {
        SmileLabDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Log.d(TAG, "🚀 Inicializando SmileLab Application...")

        // Inicializar Filament Engine (thread dedicada)
        val filamentSuccess = FilamentEngineManager.initialize()
        if (filamentSuccess) {
            Log.d(TAG, "✅ Filament Engine inicializado")
            Log.d(TAG, FilamentEngineManager.getDebugInfo())
        } else {
            Log.w(TAG, "⚠️ Filament Engine falhou (visualizador 3D não disponível)")
        }

        Log.d(TAG, "✅ SmileLab Application pronto")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "🧹 Encerrando SmileLab Application...")

        // Destruir Filament Engine
        FilamentEngineManager.destroy()
        Log.d(TAG, "✅ Filament Engine destruído")
    }
}

