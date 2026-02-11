package com.cleansoft.smilelab.filament

import android.util.Log
import com.google.android.filament.gltfio.FilamentAsset
import java.util.concurrent.ConcurrentHashMap

/**
 * Gerenciador de recursos Filament
 * Rastreia e libera assets automaticamente
 */
class ResourceManager {
    private val TAG = "ResourceManager"

    // Thread-safe map de assets
    private val assets = ConcurrentHashMap<String, FilamentAsset>()

    /**
     * Registra um asset carregado
     */
    fun registerAsset(key: String, asset: FilamentAsset) {
        assets[key] = asset
        Log.d(TAG, "✅ Asset registrado: $key (total: ${assets.size})")
    }

    /**
     * Remove asset do registro (sem destruir)
     */
    fun unregisterAsset(key: String): FilamentAsset? {
        val asset = assets.remove(key)
        if (asset != null) {
            Log.d(TAG, "📤 Asset removido do registro: $key")
        }
        return asset
    }

    /**
     * Retorna asset por chave
     */
    fun getAsset(key: String): FilamentAsset? {
        return assets[key]
    }

    /**
     * Destroi um asset específico
     */
    fun destroyAsset(key: String, modelLoader: ModelLoader) {
        val asset = unregisterAsset(key)
        if (asset != null) {
            modelLoader.destroyAsset(asset)
            Log.d(TAG, "🧹 Asset destruído: $key")
        }
    }

    /**
     * Destroi todos os assets
     */
    fun destroyAll(modelLoader: ModelLoader) {
        Log.d(TAG, "🧹 Destruindo todos os assets (${assets.size})...")

        val keys = assets.keys.toList()
        keys.forEach { key ->
            destroyAsset(key, modelLoader)
        }

        assets.clear()
        Log.d(TAG, "✅ Todos os assets destruídos")
    }

    /**
     * Retorna informações de debug
     */
    fun getDebugInfo(): String {
        return buildString {
            appendLine("ResourceManager Debug Info:")
            appendLine("  Total assets: ${assets.size}")
            assets.keys.forEach { key ->
                appendLine("    - $key")
            }
        }
    }

    /**
     * Verifica se há assets carregados
     */
    fun hasAssets(): Boolean = assets.isNotEmpty()

    /**
     * Retorna número de assets
     */
    fun getAssetCount(): Int = assets.size
}

