package com.cleansoft.smilelab.filament

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.google.android.filament.Engine
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Singleton para gerenciar o ciclo de vida do Filament Engine
 * Garante que o Engine seja criado UMA vez em uma thread dedicada
 */
object FilamentEngineManager {
    private const val TAG = "FilamentEngine"
    private const val THREAD_NAME = "FilamentGLThread"

    @Volatile
    private var engine: Engine? = null

    @Volatile
    private var handlerThread: HandlerThread? = null

    @Volatile
    private var handler: Handler? = null

    @Volatile
    private var isInitialized = false

    /**
     * Inicializa o Filament Engine em uma thread dedicada
     * Thread-safe e idempotente (pode chamar múltiplas vezes)
     */
    @Synchronized
    fun initialize(): Boolean {
        if (isInitialized) {
            Log.d(TAG, "✅ Engine já inicializado")
            return true
        }

        Log.d(TAG, "🚀 Inicializando Filament Engine...")

        try {
            // Carregar TODAS as bibliotecas nativas do Filament
            System.loadLibrary("filament-jni")
            Log.d(TAG, "✅ Biblioteca filament-jni carregada")

            System.loadLibrary("gltfio-jni")
            Log.d(TAG, "✅ Biblioteca gltfio-jni carregada")

            System.loadLibrary("filament-utils-jni")
            Log.d(TAG, "✅ Biblioteca filament-utils-jni carregada")

            // Criar thread dedicada para GL
            handlerThread = HandlerThread(THREAD_NAME).apply {
                start()
                Log.d(TAG, "✅ Thread GL criada: $THREAD_NAME")
            }

            handler = Handler(handlerThread!!.looper)

            // Criar Engine na thread GL (bloqueante)
            val latch = CountDownLatch(1)
            var success = false

            handler!!.post {
                try {
                    engine = Engine.create()
                    success = true
                    Log.d(TAG, "✅ Filament Engine criado com sucesso")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Erro ao criar Engine", e)
                } finally {
                    latch.countDown()
                }
            }

            // Aguardar criação (máximo 5 segundos)
            if (!latch.await(5, TimeUnit.SECONDS)) {
                Log.e(TAG, "❌ Timeout ao criar Engine")
                return false
            }

            if (success && engine != null) {
                isInitialized = true
                Log.d(TAG, "✅ FilamentEngineManager inicializado")
                return true
            }

            return false

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na inicialização", e)
            cleanup()
            return false
        }
    }

    /**
     * Retorna o Engine (null se não inicializado)
     */
    fun getEngine(): Engine? {
        if (!isInitialized) {
            Log.w(TAG, "⚠️ Engine não inicializado. Chame initialize() primeiro.")
        }
        return engine
    }

    /**
     * Retorna o Handler da thread GL
     */
    fun getHandler(): Handler? {
        return handler
    }

    /**
     * Retorna o Looper da thread GL
     */
    fun getLooper(): Looper? {
        return handlerThread?.looper
    }

    /**
     * Executa código na thread GL
     */
    fun runOnGLThread(block: () -> Unit) {
        handler?.post(block) ?: run {
            Log.e(TAG, "⚠️ Handler GL não disponível")
        }
    }

    /**
     * Executa código na thread GL e aguarda resultado (síncrono)
     */
    fun <T> runOnGLThreadBlocking(block: () -> T): T? {
        if (handler == null) {
            Log.e(TAG, "⚠️ Handler GL não disponível")
            return null
        }

        val latch = CountDownLatch(1)
        var result: T? = null
        var exception: Exception? = null

        handler!!.post {
            try {
                result = block()
            } catch (e: Exception) {
                exception = e
                Log.e(TAG, "❌ Erro ao executar na GL thread", e)
            } finally {
                latch.countDown()
            }
        }

        // Aguardar execução (máximo 10 segundos)
        if (!latch.await(10, TimeUnit.SECONDS)) {
            Log.e(TAG, "❌ Timeout ao executar na GL thread")
            return null
        }

        exception?.let { throw it }
        return result
    }

    /**
     * Verifica se está na thread GL
     */
    fun isOnGLThread(): Boolean {
        return Thread.currentThread() == handlerThread
    }

    /**
     * Destroi o Engine (chamar no onDestroy da Application)
     */
    @Synchronized
    fun destroy() {
        if (!isInitialized) {
            Log.d(TAG, "Engine já foi destruído ou nunca foi inicializado")
            return
        }

        Log.d(TAG, "🧹 Destruindo FilamentEngineManager...")

        // Destruir Engine na thread GL
        val latch = CountDownLatch(1)

        handler?.post {
            try {
                engine?.destroy()
                engine = null
                Log.d(TAG, "✅ Engine destruído")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao destruir Engine", e)
            } finally {
                latch.countDown()
            }
        }

        // Aguardar destruição
        latch.await(3, TimeUnit.SECONDS)

        cleanup()

        Log.d(TAG, "✅ FilamentEngineManager destruído")
    }

    /**
     * Limpa recursos
     */
    private fun cleanup() {
        handlerThread?.quitSafely()
        handlerThread = null
        handler = null
        isInitialized = false
    }

    /**
     * Informações de debug
     */
    fun getDebugInfo(): String {
        return buildString {
            appendLine("FilamentEngineManager Debug Info:")
            appendLine("  Initialized: $isInitialized")
            appendLine("  Engine: ${engine?.let { "Created" } ?: "Null"}")
            appendLine("  Thread: ${handlerThread?.name ?: "None"}")
            appendLine("  Thread alive: ${handlerThread?.isAlive}")
            appendLine("  Current thread: ${Thread.currentThread().name}")
            appendLine("  Is GL thread: ${isOnGLThread()}")
        }
    }
}

