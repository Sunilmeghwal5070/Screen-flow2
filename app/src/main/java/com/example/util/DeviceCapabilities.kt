package com.example.util

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.File
import java.text.DecimalFormat

object DeviceCapabilities {

    data class ResolutionOption(
        val label: String,
        val width: Int,
        val height: Int,
        val isSupported: Boolean,
        val recommendedBitrateMbps: Int
    )

    fun getSupportedResolutions(context: Context): List<ResolutionOption> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)

        val screenWidth = minOf(metrics.widthPixels, metrics.heightPixels)
        val screenHeight = maxOf(metrics.widthPixels, metrics.heightPixels)

        val options = mutableListOf<ResolutionOption>()

        // 480p SD
        options.add(ResolutionOption("480p SD", 480, 854, true, 4))

        // 720p HD
        options.add(ResolutionOption("720p HD", 720, 1280, true, 8))

        // 1080p FHD
        val supports1080p = screenWidth >= 1000 || isCodecSupported("video/avc", 1080, 1920)
        options.add(ResolutionOption("1080p FHD", 1080, 1920, supports1080p, 12))

        // 1440p 2K (if screen or hardware allows)
        val supports1440p = screenWidth >= 1400 || (supports1080p && screenHeight >= 2000)
        options.add(ResolutionOption("1440p 2K", 1440, 2560, supports1440p, 20))

        // Native Native Device
        options.add(
            ResolutionOption(
                "Native (${screenWidth}x${screenHeight})",
                screenWidth,
                screenHeight,
                true,
                16
            )
        )

        return options
    }

    private fun isCodecSupported(mimeType: String, width: Int, height: Int): Boolean {
        return try {
            val list = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            val format = MediaFormat.createVideoFormat(mimeType, width, height)
            list.findEncoderForFormat(format) != null
        } catch (_: Exception) {
            true
        }
    }

    fun hasMicrophone(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    fun hasCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun hasFrontCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
    }

    fun getFreeStorageBytes(context: Context): Long {
        return try {
            val dir = context.getExternalFilesDir(null) ?: context.filesDir
            val stat = StatFs(dir.path)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (_: Exception) {
            1024L * 1024L * 1024L * 8L // fallback 8 GB
        }
    }

    fun getTotalStorageBytes(context: Context): Long {
        return try {
            val dir = context.getExternalFilesDir(null) ?: context.filesDir
            val stat = StatFs(dir.path)
            stat.blockCountLong * stat.blockSizeLong
        } catch (_: Exception) {
            1024L * 1024L * 1024L * 64L // fallback 64 GB
        }
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
        val df = DecimalFormat("#,##0.#")
        return "${df.format(bytes / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
    }

    fun formatDuration(durationMs: Long): String {
        val totalSec = durationMs / 1000
        val hours = totalSec / 3600
        val mins = (totalSec % 3600) / 60
        val secs = totalSec % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }

    fun estimateRemainingRecordingMinutes(freeBytes: Long, bitrateMbps: Int): Int {
        if (bitrateMbps <= 0) return 0
        // bytesPerSecond = (bitrateMbps * 1,000,000 / 8) + audio (approx 16KB/s)
        val bytesPerSec = (bitrateMbps * 125_000L) + 16_000L
        val remainingSec = freeBytes / bytesPerSec
        return (remainingSec / 60).toInt().coerceAtLeast(0)
    }
}
