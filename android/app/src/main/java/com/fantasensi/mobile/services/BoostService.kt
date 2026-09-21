package com.fantasensi.mobile.services

import android.content.Context
import java.io.File

data class BoostResult(
    val cleanedItems: Int,
    val freedBytes: Long
)

/**
 * Limpeza legítima de cache usando apenas APIs públicas do Android.
 * Nenhuma edição de memória ou de arquivos do jogo.
 */
class BoostService(context: Context) {

    private val appContext = context.applicationContext

    fun cleanCache(): BoostResult {
        var items = 0
        var freed = 0L

        for (dir in listOf(appContext.cacheDir, appContext.externalCacheDir)) {
            if (dir == null) continue
            val before = dirSize(dir)
            val deleted = try {
                dir.deleteRecursively()
            } catch (_: Exception) {
                false
            }
            if (deleted) {
                items++
                freed += before
            }
        }
        return BoostResult(items, freed)
    }

    private fun dirSize(dir: File): Long {
        return try {
            dir.walkTopDown()
                .filter { it.isFile }
                .sumOf { it.length() }
        } catch (_: Exception) {
            0L
        }
    }
}
