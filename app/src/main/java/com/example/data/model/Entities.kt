package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val filePath: String,
    val uriString: String = "",
    val durationMs: Long = 0,
    val fileSizeBytes: Long = 0,
    val resolution: String = "1080p",
    val width: Int = 1080,
    val height: Int = 1920,
    val fps: Int = 60,
    val bitrateMbps: Int = 12,
    val audioSource: String = "Mic + Device",
    val orientation: String = "Portrait",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isScreenshot: Boolean = false,
    val thumbnailPath: String? = null,
    val eventMarkers: String = "" // comma-separated timestamps/events
)

@Entity(tableName = "presets")
data class PresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val resolution: String = "1080p",
    val width: Int = 1080,
    val height: Int = 1920,
    val fps: Int = 60,
    val bitrateMbps: Int = 12,
    val audioSource: String = "Device + Mic",
    val isFacecamEnabled: Boolean = false,
    val isTouchEnabled: Boolean = true,
    val countdownSeconds: Int = 3,
    val orientation: String = "Auto",
    val isDefault: Boolean = false,
    val iconName: String = "tutorial"
)

@Entity(tableName = "brand_kit")
data class BrandKitEntity(
    @PrimaryKey
    val id: Int = 1,
    val creatorName: String = "Creator",
    val watermarkText: String = "@screenflow",
    val watermarkPosition: String = "Bottom Right", // Top Left, Top Right, Bottom Left, Bottom Right
    val watermarkOpacity: Float = 0.8f,
    val watermarkSizeSp: Int = 14,
    val isWatermarkEnabled: Boolean = false,
    val preferredAccentTheme: String = "Indigo"
)
