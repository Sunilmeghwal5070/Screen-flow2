package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BrandKitDao
import com.example.data.dao.PresetDao
import com.example.data.dao.RecordingDao
import com.example.data.model.BrandKitEntity
import com.example.data.model.PresetEntity
import com.example.data.model.RecordingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RecordingEntity::class,
        PresetEntity::class,
        BrandKitEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ScreenFlowDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
    abstract fun presetDao(): PresetDao
    abstract fun brandKitDao(): BrandKitDao

    companion object {
        @Volatile
        private var INSTANCE: ScreenFlowDatabase? = null

        fun getDatabase(context: Context): ScreenFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScreenFlowDatabase::class.java,
                    "screenflow_studio_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getDatabase(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(database: ScreenFlowDatabase) {
            val defaultPresets = listOf(
                PresetEntity(
                    id = 1,
                    name = "YouTube Tutorial",
                    description = "1080p 60FPS with mic + device audio and touch ripples",
                    resolution = "1080p",
                    width = 1080,
                    height = 1920,
                    fps = 60,
                    bitrateMbps = 12,
                    audioSource = "Device + Mic",
                    isFacecamEnabled = false,
                    isTouchEnabled = true,
                    countdownSeconds = 3,
                    orientation = "Portrait",
                    isDefault = true,
                    iconName = "youtube"
                ),
                PresetEntity(
                    id = 2,
                    name = "Gaming Studio",
                    description = "Ultra smooth 60FPS with pure device audio capture",
                    resolution = "1080p",
                    width = 1080,
                    height = 1920,
                    fps = 60,
                    bitrateMbps = 16,
                    audioSource = "Device Audio",
                    isFacecamEnabled = true,
                    isTouchEnabled = false,
                    countdownSeconds = 3,
                    orientation = "Landscape",
                    isDefault = false,
                    iconName = "gamepad"
                ),
                PresetEntity(
                    id = 3,
                    name = "Quick Tutorial",
                    description = "Fast 720p 30FPS lightweight tutorial clip",
                    resolution = "720p",
                    width = 720,
                    height = 1280,
                    fps = 30,
                    bitrateMbps = 6,
                    audioSource = "Microphone",
                    isFacecamEnabled = false,
                    isTouchEnabled = true,
                    countdownSeconds = 3,
                    orientation = "Portrait",
                    isDefault = false,
                    iconName = "flash"
                ),
                PresetEntity(
                    id = 4,
                    name = "Ultra 1440p Master",
                    description = "High resolution 2K master quality for crisp reviews",
                    resolution = "1440p",
                    width = 1440,
                    height = 2560,
                    fps = 60,
                    bitrateMbps = 24,
                    audioSource = "Device + Mic",
                    isFacecamEnabled = false,
                    isTouchEnabled = true,
                    countdownSeconds = 5,
                    orientation = "Portrait",
                    isDefault = false,
                    iconName = "sparkles"
                ),
                PresetEntity(
                    id = 5,
                    name = "Bug Report / Demo",
                    description = "Clean screen capture with touches and voice narration",
                    resolution = "1080p",
                    width = 1080,
                    height = 1920,
                    fps = 30,
                    bitrateMbps = 8,
                    audioSource = "Microphone",
                    isFacecamEnabled = false,
                    isTouchEnabled = true,
                    countdownSeconds = 0,
                    orientation = "Auto",
                    isDefault = false,
                    iconName = "bug"
                )
            )

            database.presetDao().insertPresets(defaultPresets)

            val initialBrandKit = BrandKitEntity(
                id = 1,
                creatorName = "ScreenFlow Creator",
                watermarkText = "@screenflow",
                watermarkPosition = "Bottom Right",
                watermarkOpacity = 0.8f,
                watermarkSizeSp = 14,
                isWatermarkEnabled = false,
                preferredAccentTheme = "Indigo"
            )
            database.brandKitDao().saveBrandKit(initialBrandKit)
        }
    }
}
