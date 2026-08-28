package com.example.data.repository

import com.example.data.dao.BrandKitDao
import com.example.data.dao.PresetDao
import com.example.data.dao.RecordingDao
import com.example.data.model.BrandKitEntity
import com.example.data.model.PresetEntity
import com.example.data.model.RecordingEntity
import kotlinx.coroutines.flow.Flow
import java.io.File

class RecordingRepository(private val recordingDao: RecordingDao) {
    val allRecordings: Flow<List<RecordingEntity>> = recordingDao.getAllRecordings()
    val videosOnly: Flow<List<RecordingEntity>> = recordingDao.getVideosOnly()
    val screenshotsOnly: Flow<List<RecordingEntity>> = recordingDao.getScreenshotsOnly()
    val favoriteRecordings: Flow<List<RecordingEntity>> = recordingDao.getFavorites()
    val recentRecordings: Flow<List<RecordingEntity>> = recordingDao.getRecentRecordings()
    val totalStorageUsed: Flow<Long?> = recordingDao.getTotalAppStorageUsed()
    val screenshotStorageUsed: Flow<Long?> = recordingDao.getScreenshotStorageUsed()

    suspend fun getRecordingById(id: Long): RecordingEntity? = recordingDao.getRecordingById(id)

    suspend fun insertRecording(recording: RecordingEntity): Long = recordingDao.insertRecording(recording)

    suspend fun updateRecording(recording: RecordingEntity) = recordingDao.updateRecording(recording)

    suspend fun toggleFavorite(id: Long, current: Boolean) = recordingDao.setFavorite(id, !current)

    suspend fun renameRecording(id: Long, newTitle: String) = recordingDao.renameRecording(id, newTitle)

    suspend fun deleteRecording(recording: RecordingEntity) {
        try {
            val file = File(recording.filePath)
            if (file.exists()) file.delete()
            recording.thumbnailPath?.let {
                val thumbFile = File(it)
                if (thumbFile.exists()) thumbFile.delete()
            }
        } catch (_: Exception) {}
        recordingDao.deleteRecording(recording)
    }

    suspend fun deleteRecordingById(id: Long) {
        val entity = recordingDao.getRecordingById(id)
        if (entity != null) {
            deleteRecording(entity)
        }
    }
}

class PresetRepository(
    private val presetDao: PresetDao,
    private val brandKitDao: BrandKitDao
) {
    val allPresets: Flow<List<PresetEntity>> = presetDao.getAllPresets()
    val defaultPreset: Flow<PresetEntity?> = presetDao.getDefaultPreset()
    val brandKit: Flow<BrandKitEntity?> = brandKitDao.getBrandKit()

    suspend fun savePreset(preset: PresetEntity): Long = presetDao.insertPreset(preset)

    suspend fun updatePreset(preset: PresetEntity) = presetDao.updatePreset(preset)

    suspend fun deletePreset(preset: PresetEntity) = presetDao.deletePreset(preset)

    suspend fun setDefaultPreset(id: Long) {
        presetDao.clearDefaultPreset()
        presetDao.setDefaultPreset(id)
    }

    suspend fun saveBrandKit(brandKit: BrandKitEntity) = brandKitDao.saveBrandKit(brandKit)
}
