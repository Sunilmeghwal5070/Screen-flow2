package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object VideoMetadataHelper {

    data class VideoInfo(
        val durationMs: Long,
        val width: Int,
        val height: Int,
        val bitrate: Long,
        val mimeType: String
    )

    fun extractVideoInfo(filePath: String): VideoInfo {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val bitrateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
            val mimeStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: "video/mp4"

            VideoInfo(
                durationMs = durationStr?.toLongOrNull() ?: 0L,
                width = widthStr?.toIntOrNull() ?: 1080,
                height = heightStr?.toIntOrNull() ?: 1920,
                bitrate = bitrateStr?.toLongOrNull() ?: 8_000_000L,
                mimeType = mimeStr
            )
        } catch (_: Exception) {
            VideoInfo(0L, 1080, 1920, 8_000_000L, "video/mp4")
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }
    }

    fun generateThumbnail(context: Context, videoPath: String, timestampMs: Long = 1000L): String? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(videoPath)
            val bitmap = retriever.getFrameAtTime(timestampMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime

            if (bitmap != null) {
                val thumbsDir = File(context.filesDir, "thumbnails").apply { mkdirs() }
                val thumbFile = File(thumbsDir, "thumb_${System.currentTimeMillis()}.jpg")
                FileOutputStream(thumbFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                bitmap.recycle()
                thumbFile.absolutePath
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }
    }

    fun generateFileName(prefix: String = "ScreenFlow", extension: String = "mp4"): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        return "${prefix}_${sdf.format(Date())}.$extension"
    }

    fun formatDate(timestampMs: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
        return sdf.format(Date(timestampMs))
    }
}
