package com.fantasensi.mobile.services

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import java.io.File
import java.util.Locale

data class StatsSnapshot(
    val ramTotalBytes: Long,
    val ramUsedBytes: Long,
    val ramLoadPercent: Int,
    val storageTotalBytes: Long,
    val storageFreeBytes: Long,
    val storageUsedPercent: Int,
    val batteryPercent: Int,
    val batteryCharging: Boolean,
    val cpuPercent: Int
)

class SystemStatsService(context: Context) {

    private val appContext = context.applicationContext
    private val activityManager =
        appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private var prevTotal = 0L
    private var prevIdle = 0L

    fun snapshot(): StatsSnapshot {
        val ram = ramStats()
        val storage = storageStats()
        val battery = batteryStats()
        val cpu = cpuStats()
        return StatsSnapshot(
            ramTotalBytes = ram.first,
            ramUsedBytes = ram.second,
            ramLoadPercent = ram.third,
            storageTotalBytes = storage.first,
            storageFreeBytes = storage.second,
            storageUsedPercent = storage.third,
            batteryPercent = battery.first,
            batteryCharging = battery.second,
            cpuPercent = cpu
        )
    }

    private fun ramStats(): Triple<Long, Long, Int> {
        val mem = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(mem)
        val total = mem.totalMem
        val avail = mem.availMem
        val used = total - avail
        val percent = if (total > 0) ((used * 100) / total).toInt() else 0
        return Triple(total, used, percent.coerceIn(0, 100))
    }

    private fun storageStats(): Triple<Long, Long, Int> {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val total = stat.totalBytes
        val free = stat.availableBytes
        val used = total - free
        val percent = if (total > 0) ((used * 100) / total).toInt() else 0
        return Triple(total, free, percent.coerceIn(0, 100))
    }

    private fun batteryStats(): Pair<Int, Boolean> {
        val intent = try {
            appContext.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
        } catch (_: Exception) {
            null
        }
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val percent = if (scale > 0) (level * 100) / scale else 0
        return percent.coerceIn(0, 100) to charging
    }

    private fun cpuStats(): Int {
        return try {
            val reader = File("/proc/stat").bufferedReader()
            val line = reader.useLines { it.firstOrNull() ?: "" }
            val parts = line.split(Regex("\\s+")).drop(1).mapNotNull { it.toLongOrNull() }
            if (parts.size < 4) return 0
            val idle = parts[3] + (parts.getOrNull(4) ?: 0L)
            // user, nice, system, idle, iowait, irq, softirq, steal (guest/guest_nice já estão em user/nice)
            val total = parts.take(8).sum()
            val dTotal = total - prevTotal
            val dIdle = idle - prevIdle
            prevTotal = total
            prevIdle = idle
            if (dTotal <= 0) 0 else ((100 * (dTotal - dIdle)) / dTotal).toInt().coerceIn(0, 100)
        } catch (_: Exception) {
            0
        }
    }
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var i = 0
    while (value >= 1024 && i < units.size - 1) {
        value /= 1024
        i++
    }
    return if (i == 0) String.format(Locale.US, "%.0f %s", value, units[i])
    else String.format(Locale.US, "%.1f %s", value, units[i])
}
