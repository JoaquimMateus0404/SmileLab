package com.cleansoft.smilelab.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

/**
 * Carregador de modelos 3D seguindo as melhores práticas do Filament
 * TODAS as operações de Filament ocorrem na thread GL
 */
class ModelLoader(
    private val context: Context
) {
    private val TAG = "ModelLoader"

    // Estes serão criados na GL thread
    private var assetLoader: AssetLoader? = null
    private var resourceLoader: ResourceLoader? = null
    private var isInitialized = false

    /**
     * Inicializa o ModelLoader NA THREAD GL
     * Deve ser chamado uma vez antes de loadModel
     */
    fun initialize(engine: Engine) {
        if (isInitialized) {
            Log.d(TAG, "✅ ModelLoader já inicializado")
            return
        }

        // CRITICAL: Criar AssetLoader e ResourceLoader na thread GL
        FilamentEngineManager.runOnGLThread {
            try {
                val materialProvider = UbershaderProvider(engine)
                assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())
                resourceLoader = ResourceLoader(engine)
                isInitialized = true
                Log.d(TAG, "✅ ModelLoader inicializado na GL thread")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao inicializar ModelLoader", e)
            }
        }
    }

    /**
     * Carrega modelo de forma assíncrona
     * @param path Caminho do modelo em assets (ex: "models/scene.gltf")
     * @return FilamentAsset ou null se falhar
     */
    suspend fun loadModel(path: String): FilamentAsset? {
        Log.d(TAG, "📦 Iniciando carregamento: $path")

        if (!isInitialized) {
            Log.e(TAG, "❌ ModelLoader não inicializado. Chame initialize() primeiro.")
            return null
        }

        try {
            // Fase 1: Carregar bytes do arquivo (IO thread)
            val bytes = loadBytesFromAssets(path)
                ?: return null

            Log.d(TAG, "✅ Arquivo carregado: ${bytes.size} bytes")

            // Fase 2: Criar asset NA GL THREAD (bloqueante)
            val asset = createAssetOnGLThread(bytes)
                ?: return null

            Log.d(TAG, "✅ Asset criado")

            // Fase 3: Carregar recursos NA GL THREAD (bloqueante)
            loadResourcesOnGLThread(asset)

            Log.d(TAG, "✅ Modelo carregado com sucesso: $path")

            return asset

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao carregar modelo: $path", e)
            return null
        }
    }

    /**
     * Carrega bytes do arquivo de forma assíncrona (IO thread)
     */
    private suspend fun loadBytesFromAssets(path: String): ByteArray? = withContext(Dispatchers.IO) {
        try {
            // Verificar se arquivo existe
            val directory = if (path.contains('/')) {
                path.substringBeforeLast('/')
            } else {
                ""
            }

            val fileName = path.substringAfterLast('/')

            val fileList = if (directory.isNotEmpty()) {
                context.assets.list(directory)
            } else {
                context.assets.list("")
            }

            if (fileList?.contains(fileName) != true) {
                Log.w(TAG, "⚠️ Arquivo não encontrado: $path")
                Log.w(TAG, "Arquivos disponíveis em '$directory': ${fileList?.joinToString()}")
                return@withContext null
            }

            // Carregar arquivo
            context.assets.open(path).use { inputStream ->
                inputStream.readBytes()
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao ler arquivo: $path", e)
            null
        }
    }

    /**
     * Cria FilamentAsset NA THREAD GL (bloqueante)
     * CRITICAL: Esta operação DEVE ocorrer na GL thread
     */
    private suspend fun createAssetOnGLThread(bytes: ByteArray): FilamentAsset? =
        withContext(Dispatchers.IO) {
            try {
                // Executar NA THREAD GL e aguardar resultado
                FilamentEngineManager.runOnGLThreadBlocking {
                    val buffer = ByteBuffer.allocateDirect(bytes.size)
                    buffer.put(bytes)
                    buffer.flip()

                    // AssetLoader.createAsset deve ser chamado NA THREAD GL
                    val loader = assetLoader ?: throw IllegalStateException("AssetLoader não inicializado")
                    loader.createAsset(buffer)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao criar asset", e)
                null
            }
        }

    /**
     * Carrega recursos do asset NA THREAD GL (bloqueante)
     * CRITICAL: ResourceLoader.loadResources deve ser chamado NA THREAD GL
     */
    private suspend fun loadResourcesOnGLThread(asset: FilamentAsset) =
        withContext(Dispatchers.IO) {
            FilamentEngineManager.runOnGLThreadBlocking {
                try {
                    Log.d(TAG, "📥 Carregando recursos...")

                    val loader = resourceLoader ?: throw IllegalStateException("ResourceLoader não inicializado")

                    // Carregar recursos de forma síncrona (já estamos na GL thread)
                    loader.loadResources(asset)

                    // Liberar dados fonte (economizar memória)
                    asset.releaseSourceData()

                    Log.d(TAG, "✅ Recursos carregados")

                } catch (e: Exception) {
                    Log.e(TAG, "❌ Erro ao carregar recursos", e)
                    throw e
                }
            }
        }

    /**
     * Destroi asset NA THREAD GL
     */
    fun destroyAsset(asset: FilamentAsset) {
        FilamentEngineManager.runOnGLThread {
            try {
                assetLoader?.destroyAsset(asset)
                Log.d(TAG, "✅ Asset destruído")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao destruir asset", e)
            }
        }
    }

    /**
     * Cleanup - destroi loaders NA THREAD GL
     */
    fun destroy() {
        FilamentEngineManager.runOnGLThread {
            try {
                assetLoader?.destroy()
                assetLoader = null
                resourceLoader = null
                isInitialized = false
                Log.d(TAG, "✅ ModelLoader destruído")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao destruir ModelLoader", e)
            }
        }
    }
}

