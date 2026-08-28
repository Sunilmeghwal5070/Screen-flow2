package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BrandKitEntity
import com.example.data.model.PresetEntity
import com.example.data.model.RecordingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {
    @Query("SELECT * FROM recordings ORDER BY timestamp DESC")
    fun getAllRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE isScreenshot = 0 ORDER BY timestamp DESC")
    fun getVideosOnly(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE isScreenshot = 1 ORDER BY timestamp DESC")
    fun getScreenshotsOnly(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings ORDER BY timestamp DESC LIMIT 3")
    fun getRecentRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getRecordingById(id: Long): RecordingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: RecordingEntity): Long

    @Update
    suspend fun updateRecording(recording: RecordingEntity)

    @Query("UPDATE recordings SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE recordings SET title = :newTitle WHERE id = :id")
    suspend fun renameRecording(id: Long, newTitle: String)

    @Delete
    suspend fun deleteRecording(recording: RecordingEntity)

    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteRecordingById(id: Long)

    @Query("SELECT SUM(fileSizeBytes) FROM recordings")
    fun getTotalAppStorageUsed(): Flow<Long?>

    @Query("SELECT SUM(fileSizeBytes) FROM recordings WHERE isScreenshot = 1")
    fun getScreenshotStorageUsed(): Flow<Long?>
}

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets ORDER BY isDefault DESC, id ASC")
    fun getAllPresets(): Flow<List<PresetEntity>>

    @Query("SELECT * FROM presets WHERE isDefault = 1 LIMIT 1")
    fun getDefaultPreset(): Flow<PresetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: PresetEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPresets(presets: List<PresetEntity>)

    @Update
    suspend fun updatePreset(preset: PresetEntity)

    @Delete
    suspend fun deletePreset(preset: PresetEntity)

    @Query("UPDATE presets SET isDefault = 0")
    suspend fun clearDefaultPreset()

    @Query("UPDATE presets SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultPreset(id: Long)
}

@Dao
interface BrandKitDao {
    @Query("SELECT * FROM brand_kit WHERE id = 1")
    fun getBrandKit(): Flow<BrandKitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBrandKit(brandKit: BrandKitEntity)
}
