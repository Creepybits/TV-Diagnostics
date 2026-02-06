package com.example.tvdiagnostics

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log

data class DiagnosticInfo(
    val manufacturer: String,
    val model: String,
    val sdkVersion: Int,
    val androidVersion: String,
    val ramTotal: String,
    val ramAvailable: String,
    val processCount: Int
)

class DiagnosticsManager(private val context: Context) {

    companion object {
        private const val TAG = "DiagnosticsManager"
    }

    fun getDiagnostics(): DiagnosticInfo {
        Log.i(TAG, "Diagnostic Check Started")
        
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val sdkVersion = Build.VERSION.SDK_INT
        val androidVersion = Build.VERSION.RELEASE
        
        // Memory Info
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val ramTotal = formatSize(memoryInfo.totalMem)
        val ramAvailable = formatSize(memoryInfo.availMem)
        
        // Process Count (Best Effort)
        val runningAppProcesses = activityManager.runningAppProcesses ?: emptyList()
        val processCount = runningAppProcesses.size

        Log.i(TAG, "Device Info:")
        Log.i(TAG, "Manufacturer: $manufacturer")
        Log.i(TAG, "Model: $model")
        Log.i(TAG, "SDK Version: $sdkVersion")
        Log.i(TAG, "Android Version: $androidVersion")
        Log.i(TAG, "RAM Total: $ramTotal")
        Log.i(TAG, "RAM Available: $ramAvailable")
        Log.i(TAG, "Running Processes: $processCount")
        
        return DiagnosticInfo(
            manufacturer = manufacturer,
            model = model,
            sdkVersion = sdkVersion,
            androidVersion = androidVersion,
            ramTotal = ramTotal,
            ramAvailable = ramAvailable,
            processCount = processCount
        )
    }

    fun quickClean(): Int {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return 0
        var killedCount = 0
        
        for (processInfo in runningAppProcesses) {
            // Don't kill self
            if (processInfo.processName != context.packageName) {
                // Determine package name (usually process name is package name)
                val packageName = processInfo.processName.split(":")[0]
                try {
                    activityManager.killBackgroundProcesses(packageName)
                    killedCount++
                    Log.d(TAG, "Attempted to kill: $packageName")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to kill $packageName", e)
                }
            }
        }
        return killedCount
    }

    private fun formatSize(bytes: Long): String {
        val kb = bytes / 1024
        val mb = kb / 1024
        val gb = mb / 1024
        return if (gb > 0) "$gb GB" else "$mb MB"
    }
}
