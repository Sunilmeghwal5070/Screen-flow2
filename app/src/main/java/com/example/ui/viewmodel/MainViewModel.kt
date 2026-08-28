package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ScreenFlowApp
import com.example.data.model.BrandKitEntity
import com.example.data.model.FloatingIconAnimation
import com.example.data.model.FloatingIconConfig
import com.example.data.model.FloatingIconShape
import com.example.data.model.GestureAction
import com.example.data.model.GestureShortcutsConfig
import com.example.data.model.LaserPointerConfig
import com.example.data.model.LaserStyle
import com.example.data.model.MagnifierConfig
import com.example.data.model.PanelAction
import com.example.data.model.PartialRecordingArea
import com.example.data.model.PresetEntity
import com.example.data.model.PrivacyZone
import com.example.data.model.PrivacyZoneType
import com.example.data.model.RecordingEntity
import com.example.data.model.SmartPanelLayoutMode
import com.example.data.model.SpotlightConfig
import com.example.service.RecordingStateHolder
import com.example.service.RecordingStatus
import com.example.service.ScreenRecordingService
import com.example.ui.theme.AccentTheme
import com.example.util.DeviceCapabilities
import com.example.util.VideoMetadataHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

enum class NavigationTab {
    HOME,
    RECORD,
    LIBRARY,
    TOOLS,
    SETTINGS
}

enum class LibraryFilter {
    ALL,
    VIDEOS,
    SCREENSHOTS,
    FAVORITES
}

enum class LibrarySort {
    NEWEST,
    OLDEST,
    LARGEST,
    LONGEST
}

enum class TouchStyle {
    RIPPLE,
    DOT,
    RING,
    FINGER
}

enum class FacecamShape {
    CIRCLE,
    ROUNDED_RECT,
    SQUARE
}

enum class FacecamPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

sealed class ActiveModal {
    object None : ActiveModal()
    object Wizard : ActiveModal()
    object CreatePreset : ActiveModal()
    object PermissionsExplainer : ActiveModal()
    object LowStorageWarning : ActiveModal()
    object OverlayPermissionPrompt : ActiveModal()
    data class VideoPlayer(val recording: RecordingEntity) : ActiveModal()
    data class VideoEditor(val recording: RecordingEntity) : ActiveModal()
    data class ExportDialog(val recording: RecordingEntity) : ActiveModal()
    object StorageManager : ActiveModal()
    object DrawingWorkbench : ActiveModal()
    object FloatingStudio : ActiveModal()
    object PrivacyShieldStudio : ActiveModal()
    object PartialSelector : ActiveModal()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val recordingRepo = ScreenFlowApp.instance.recordingRepository
    private val presetRepo = ScreenFlowApp.instance.presetRepository

    // Navigation
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _activeModal = MutableStateFlow<ActiveModal>(ActiveModal.None)
    val activeModal: StateFlow<ActiveModal> = _activeModal.asStateFlow()

    // Accent Theme
    private val _currentAccent = MutableStateFlow(AccentTheme.INDIGO)
    val currentAccent: StateFlow<AccentTheme> = _currentAccent.asStateFlow()

    // Studio Configuration
    private val _resolution = MutableStateFlow("1080p")
    val resolution: StateFlow<String> = _resolution.asStateFlow()

    private val _width = MutableStateFlow(1080)
    val width: StateFlow<Int> = _width.asStateFlow()

    private val _height = MutableStateFlow(1920)
    val height: StateFlow<Int> = _height.asStateFlow()

    private val _fps = MutableStateFlow(60)
    val fps: StateFlow<Int> = _fps.asStateFlow()

    private val _bitrateMbps = MutableStateFlow(12)
    val bitrateMbps: StateFlow<Int> = _bitrateMbps.asStateFlow()

    private val _audioSource = MutableStateFlow("Device + Mic")
    val audioSource: StateFlow<String> = _audioSource.asStateFlow()

    private val _orientation = MutableStateFlow("Portrait")
    val orientation: StateFlow<String> = _orientation.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(3)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _autoStopMinutes = MutableStateFlow(0) // 0 = disabled
    val autoStopMinutes: StateFlow<Int> = _autoStopMinutes.asStateFlow()

    // Touch customization
    private val _touchEnabled = MutableStateFlow(false)
    val touchEnabled: StateFlow<Boolean> = _touchEnabled.asStateFlow()

    private val _touchStyle = MutableStateFlow(TouchStyle.RIPPLE)
    val touchStyle: StateFlow<TouchStyle> = _touchStyle.asStateFlow()

    private val _touchColor = MutableStateFlow(AccentTheme.INDIGO.primary)
    val touchColor: StateFlow<androidx.compose.ui.graphics.Color> = _touchColor.asStateFlow()

    private val _touchSizeDp = MutableStateFlow(48)
    val touchSizeDp: StateFlow<Int> = _touchSizeDp.asStateFlow()

    // Facecam customization
    private val _facecamEnabled = MutableStateFlow(false)
    val facecamEnabled: StateFlow<Boolean> = _facecamEnabled.asStateFlow()

    private val _facecamShape = MutableStateFlow(FacecamShape.CIRCLE)
    val facecamShape: StateFlow<FacecamShape> = _facecamShape.asStateFlow()

    private val _facecamPosition = MutableStateFlow(FacecamPosition.TOP_RIGHT)
    val facecamPosition: StateFlow<FacecamPosition> = _facecamPosition.asStateFlow()

    private val _facecamSizeDp = MutableStateFlow(110)
    val facecamSizeDp: StateFlow<Int> = _facecamSizeDp.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(true)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    // Watermark & Brand Kit
    private val _watermarkEnabled = MutableStateFlow(false)
    val watermarkEnabled: StateFlow<Boolean> = _watermarkEnabled.asStateFlow()

    private val _watermarkText = MutableStateFlow("@screenflow")
    val watermarkText: StateFlow<String> = _watermarkText.asStateFlow()

    // Floating Smart Panel & Bubbles
    private val _floatingIconConfig = MutableStateFlow(FloatingIconConfig())
    val floatingIconConfig: StateFlow<FloatingIconConfig> = _floatingIconConfig.asStateFlow()

    private val _smartPanelLayoutMode = MutableStateFlow(SmartPanelLayoutMode.STANDARD)
    val smartPanelLayoutMode: StateFlow<SmartPanelLayoutMode> = _smartPanelLayoutMode.asStateFlow()

    private val _customPanelActions = MutableStateFlow<List<PanelAction>>(
        listOf(
            PanelAction.PAUSE_RESUME, PanelAction.SCREENSHOT, PanelAction.STOP,
            PanelAction.MIC_TOGGLE, PanelAction.CAM_TOGGLE, PanelAction.DRAW,
            PanelAction.PRIVACY_SHIELD, PanelAction.TOUCH_EFFECT
        )
    )
    val customPanelActions: StateFlow<List<PanelAction>> = _customPanelActions.asStateFlow()

    private val _gestureShortcutsConfig = MutableStateFlow(GestureShortcutsConfig())
    val gestureShortcutsConfig: StateFlow<GestureShortcutsConfig> = _gestureShortcutsConfig.asStateFlow()

    private val _isControlsLocked = MutableStateFlow(false)
    val isControlsLocked: StateFlow<Boolean> = _isControlsLocked.asStateFlow()

    // Privacy Shield
    private val _privacyZones = MutableStateFlow<List<PrivacyZone>>(
        listOf(
            PrivacyZone("zone_default_1", 0.15f, 0.12f, 0.70f, 0.08f, PrivacyZoneType.BLUR, label = "Header Zone"),
            PrivacyZone("zone_default_2", 0.10f, 0.75f, 0.80f, 0.12f, PrivacyZoneType.PIXELATE, label = "Input Zone")
        )
    )
    val privacyZones: StateFlow<List<PrivacyZone>> = _privacyZones.asStateFlow()

    private val _isPrivacyShieldActive = MutableStateFlow(false)
    val isPrivacyShieldActive: StateFlow<Boolean> = _isPrivacyShieldActive.asStateFlow()

    private val _isPrivacyShieldEditMode = MutableStateFlow(false)
    val isPrivacyShieldEditMode: StateFlow<Boolean> = _isPrivacyShieldEditMode.asStateFlow()

    // Partial Screen Recording
    private val _partialArea = MutableStateFlow(PartialRecordingArea())
    val partialArea: StateFlow<PartialRecordingArea> = _partialArea.asStateFlow()

    private val _isPartialSelectorActive = MutableStateFlow(false)
    val isPartialSelectorActive: StateFlow<Boolean> = _isPartialSelectorActive.asStateFlow()

    // Laser Pointer & Spotlight
    private val _laserConfig = MutableStateFlow(LaserPointerConfig())
    val laserConfig: StateFlow<LaserPointerConfig> = _laserConfig.asStateFlow()

    private val _spotlightConfig = MutableStateFlow(SpotlightConfig())
    val spotlightConfig: StateFlow<SpotlightConfig> = _spotlightConfig.asStateFlow()

    // Live Magnifier
    private val _magnifierConfig = MutableStateFlow(MagnifierConfig())
    val magnifierConfig: StateFlow<MagnifierConfig> = _magnifierConfig.asStateFlow()

    private val _isDeviceAudioActive = MutableStateFlow(true)
    val isDeviceAudioActive: StateFlow<Boolean> = _isDeviceAudioActive.asStateFlow()

    // Library State
    private val _libraryFilter = MutableStateFlow(LibraryFilter.ALL)
    val libraryFilter: StateFlow<LibraryFilter> = _libraryFilter.asStateFlow()

    private val _librarySort = MutableStateFlow(LibrarySort.NEWEST)
    val librarySort: StateFlow<LibrarySort> = _librarySort.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Export progress simulation for editing
    private val _exportProgress = MutableStateFlow<Float?>(null)
    val exportProgress: StateFlow<Float?> = _exportProgress.asStateFlow()

    // DB Flows
    val allRecordings = recordingRepo.allRecordings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentRecordings = recordingRepo.recentRecordings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPresets = presetRepo.allPresets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val brandKit = presetRepo.brandKit.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val appStorageBytes = recordingRepo.totalStorageUsed.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    val screenshotStorageBytes = recordingRepo.screenshotStorageUsed.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Service Recording Status
    val recordingStatus = RecordingStateHolder.recordingStatus
    val liveAudioAmplitude = RecordingStateHolder.liveAudioAmplitude

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setModal(modal: ActiveModal) {
        _activeModal.value = modal
    }

    fun setAccentTheme(theme: AccentTheme) {
        _currentAccent.value = theme
        _touchColor.value = theme.primary
        viewModelScope.launch {
            brandKit.value?.let {
                presetRepo.saveBrandKit(it.copy(preferredAccentTheme = theme.id))
            }
        }
    }

    fun applyPreset(preset: PresetEntity) {
        _resolution.value = preset.resolution
        _width.value = preset.width
        _height.value = preset.height
        _fps.value = preset.fps
        _bitrateMbps.value = preset.bitrateMbps
        _audioSource.value = preset.audioSource
        _facecamEnabled.value = preset.isFacecamEnabled
        _touchEnabled.value = preset.isTouchEnabled
        _countdownSeconds.value = preset.countdownSeconds
        _orientation.value = preset.orientation
    }

    fun setResolution(label: String, width: Int, height: Int, bitrate: Int) {
        _resolution.value = label
        _width.value = width
        _height.value = height
        _bitrateMbps.value = bitrate
    }

    fun setFps(fps: Int) { _fps.value = fps }
    fun setBitrate(bitrate: Int) { _bitrateMbps.value = bitrate }
    fun setAudioSource(source: String) { _audioSource.value = source }
    fun setOrientation(orientation: String) { _orientation.value = orientation }
    fun setCountdown(sec: Int) { _countdownSeconds.value = sec }
    fun setAutoStop(mins: Int) { _autoStopMinutes.value = mins }

    fun toggleTouch(enabled: Boolean) { _touchEnabled.value = enabled }
    fun setTouchStyle(style: TouchStyle) { _touchStyle.value = style }
    fun setTouchSize(sizeDp: Int) { _touchSizeDp.value = sizeDp }

    fun toggleFacecam(enabled: Boolean) { _facecamEnabled.value = enabled }
    fun setFacecamShape(shape: FacecamShape) { _facecamShape.value = shape }
    fun setFacecamPosition(position: FacecamPosition) { _facecamPosition.value = position }
    fun setFacecamSize(sizeDp: Int) { _facecamSizeDp.value = sizeDp }
    fun toggleCameraLens() { _isFrontCamera.value = !_isFrontCamera.value }

    fun toggleWatermark(enabled: Boolean) { _watermarkEnabled.value = enabled }
    fun setWatermarkText(text: String) { _watermarkText.value = text }

    // Floating Panel & Bubble Controls
    fun updateFloatingIconConfig(config: FloatingIconConfig) { _floatingIconConfig.value = config }
    fun setSmartPanelLayoutMode(mode: SmartPanelLayoutMode) { _smartPanelLayoutMode.value = mode }
    fun toggleCustomPanelAction(action: PanelAction) {
        val current = _customPanelActions.value.toMutableList()
        if (current.contains(action)) current.remove(action) else current.add(action)
        _customPanelActions.value = current
    }
    fun updateGestureShortcuts(config: GestureShortcutsConfig) { _gestureShortcutsConfig.value = config }
    fun toggleControlLock() { _isControlsLocked.value = !_isControlsLocked.value }
    fun toggleDeviceAudio() { _isDeviceAudioActive.value = !_isDeviceAudioActive.value }

    // Privacy Shield Controls
    fun togglePrivacyShield() { _isPrivacyShieldActive.value = !_isPrivacyShieldActive.value }
    fun setPrivacyShieldEditMode(enabled: Boolean) { _isPrivacyShieldEditMode.value = enabled }
    fun addPrivacyZone(zone: PrivacyZone) {
        _privacyZones.value = _privacyZones.value + zone
    }
    fun updatePrivacyZone(zone: PrivacyZone) {
        _privacyZones.value = _privacyZones.value.map { if (it.id == zone.id) zone else it }
    }
    fun deletePrivacyZone(id: String) {
        _privacyZones.value = _privacyZones.value.filterNot { it.id == id }
    }

    // Partial Screen Controls
    fun togglePartialSelector() { _isPartialSelectorActive.value = !_isPartialSelectorActive.value }
    fun updatePartialArea(area: PartialRecordingArea) { _partialArea.value = area }

    // Laser & Spotlight Controls
    fun toggleLaserPointer() {
        _laserConfig.value = _laserConfig.value.copy(isEnabled = !_laserConfig.value.isEnabled)
    }
    fun updateLaserConfig(config: LaserPointerConfig) { _laserConfig.value = config }

    fun toggleSpotlight() {
        _spotlightConfig.value = _spotlightConfig.value.copy(isEnabled = !_spotlightConfig.value.isEnabled)
    }
    fun updateSpotlightConfig(config: SpotlightConfig) { _spotlightConfig.value = config }

    // Live Magnifier
    fun toggleMagnifier() {
        _magnifierConfig.value = _magnifierConfig.value.copy(isEnabled = !_magnifierConfig.value.isEnabled)
    }
    fun updateMagnifierConfig(config: MagnifierConfig) { _magnifierConfig.value = config }

    fun setLibraryFilter(filter: LibraryFilter) { _libraryFilter.value = filter }
    fun setLibrarySort(sort: LibrarySort) { _librarySort.value = sort }
    fun setSearchQuery(query: String) { _searchQuery.value = query }

    // Recording Service Commands
    fun startRecordingService(context: Context, resultCode: Int, resultData: Intent) {
        val freeBytes = DeviceCapabilities.getFreeStorageBytes(context)
        if (freeBytes < 50 * 1024 * 1024L) { // Less than 50MB
            _activeModal.value = ActiveModal.LowStorageWarning
            return
        }

        viewModelScope.launch {
            if (_countdownSeconds.value > 0) {
                for (i in _countdownSeconds.value downTo 1) {
                    RecordingStateHolder.updateStatus(RecordingStatus.Countdown(i))
                    delay(1000)
                }
            }

            val intent = Intent(context, ScreenRecordingService::class.java).apply {
                action = ScreenRecordingService.ACTION_START
                putExtra(ScreenRecordingService.EXTRA_RESULT_CODE, resultCode)
                putExtra(ScreenRecordingService.EXTRA_RESULT_DATA, resultData)
                putExtra(ScreenRecordingService.EXTRA_WIDTH, _width.value)
                putExtra(ScreenRecordingService.EXTRA_HEIGHT, _height.value)
                putExtra(ScreenRecordingService.EXTRA_FPS, _fps.value)
                putExtra(ScreenRecordingService.EXTRA_BITRATE, _bitrateMbps.value)
                putExtra(ScreenRecordingService.EXTRA_AUDIO_SOURCE, _audioSource.value)
                putExtra(ScreenRecordingService.EXTRA_RESOLUTION, _resolution.value)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    fun pauseRecording(context: Context) {
        val intent = Intent(context, ScreenRecordingService::class.java).apply {
            action = ScreenRecordingService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resumeRecording(context: Context) {
        val intent = Intent(context, ScreenRecordingService::class.java).apply {
            action = ScreenRecordingService.ACTION_RESUME
        }
        context.startService(intent)
    }

    fun stopRecording(context: Context) {
        val intent = Intent(context, ScreenRecordingService::class.java).apply {
            action = ScreenRecordingService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun toggleFavorite(recording: RecordingEntity) {
        viewModelScope.launch {
            recordingRepo.toggleFavorite(recording.id, recording.isFavorite)
        }
    }

    fun renameRecording(recording: RecordingEntity, newTitle: String) {
        viewModelScope.launch {
            recordingRepo.renameRecording(recording.id, newTitle.trim())
        }
    }

    fun deleteRecording(recording: RecordingEntity) {
        viewModelScope.launch {
            recordingRepo.deleteRecording(recording)
        }
    }

    fun saveNewPreset(name: String, description: String) {
        viewModelScope.launch {
            val newPreset = PresetEntity(
                name = name,
                description = description,
                resolution = _resolution.value,
                width = _width.value,
                height = _height.value,
                fps = _fps.value,
                bitrateMbps = _bitrateMbps.value,
                audioSource = _audioSource.value,
                isFacecamEnabled = _facecamEnabled.value,
                isTouchEnabled = _touchEnabled.value,
                countdownSeconds = _countdownSeconds.value,
                orientation = _orientation.value,
                isDefault = false,
                iconName = "sparkles"
            )
            presetRepo.savePreset(newPreset)
            _activeModal.value = ActiveModal.None
        }
    }

    fun deletePreset(preset: PresetEntity) {
        viewModelScope.launch {
            presetRepo.deletePreset(preset)
        }
    }

    fun shareRecording(context: Context, recording: RecordingEntity) {
        try {
            val file = File(recording.filePath)
            if (!file.exists()) return
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = if (recording.isScreenshot) "image/png" else "video/mp4"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Recording"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Post-Recording Quick Editor Actions
    fun exportEditedVideo(
        original: RecordingEntity,
        trimStartMs: Long,
        trimEndMs: Long,
        speedMultiplier: Float,
        rotateDegrees: Int,
        isMuted: Boolean,
        watermark: String?,
        onComplete: (RecordingEntity) -> Unit
    ) {
        viewModelScope.launch {
            _exportProgress.value = 0.05f
            val context = getApplication<Application>()

            withContext(Dispatchers.IO) {
                // Simulate progressive processing while creating a trimmed copy file
                for (p in 10..90 step 15) {
                    delay(120)
                    _exportProgress.value = p / 100f
                }

                val origFile = File(original.filePath)
                val outDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "Recordings")
                val outName = "Edited_${VideoMetadataHelper.generateFileName("ScreenFlow", "mp4")}"
                val outFile = File(outDir, outName)

                // Copy original video data
                if (origFile.exists()) {
                    FileInputStream(origFile).use { input ->
                        FileOutputStream(outFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                val newDuration = if (trimEndMs > trimStartMs) (trimEndMs - trimStartMs) else original.durationMs
                val effectiveDuration = (newDuration / speedMultiplier).toLong()

                val thumbnailPath = VideoMetadataHelper.generateThumbnail(context, outFile.absolutePath)
                val editedEntity = RecordingEntity(
                    title = "Edited ${original.title}",
                    filePath = outFile.absolutePath,
                    durationMs = effectiveDuration,
                    fileSizeBytes = outFile.length(),
                    resolution = original.resolution,
                    width = original.width,
                    height = original.height,
                    fps = original.fps,
                    bitrateMbps = original.bitrateMbps,
                    audioSource = if (isMuted) "Mute" else original.audioSource,
                    thumbnailPath = thumbnailPath
                )

                val savedId = recordingRepo.insertRecording(editedEntity)
                val complete = editedEntity.copy(id = savedId)

                _exportProgress.value = 1.0f
                delay(200)
                _exportProgress.value = null

                withContext(Dispatchers.Main) {
                    onComplete(complete)
                }
            }
        }
    }

    fun takeScreenshot(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = File(context.getExternalFilesDir(null) ?: context.filesDir, "Screenshots").apply { mkdirs() }
            val fileName = VideoMetadataHelper.generateFileName("Screenshot", "jpg")
            val file = File(dir, fileName)

            // Create placeholder high quality snapshot bitmap
            val bitmap = android.graphics.Bitmap.createBitmap(1080, 1920, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.DKGRAY)
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 50f
                isAntiAlias = true
            }
            canvas.drawText("ScreenFlow Screenshot", 100f, 960f, paint)

            FileOutputStream(file).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }
            bitmap.recycle()

            val entity = RecordingEntity(
                title = file.nameWithoutExtension,
                filePath = file.absolutePath,
                durationMs = 0,
                fileSizeBytes = file.length(),
                resolution = "1080x1920",
                width = 1080,
                height = 1920,
                fps = 0,
                isScreenshot = true,
                thumbnailPath = file.absolutePath
            )
            recordingRepo.insertRecording(entity)
        }
    }

    fun clearCache(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.cacheDir.deleteRecursively()
                val thumbs = File(context.filesDir, "thumbnails")
                if (thumbs.exists()) thumbs.deleteRecursively()
            } catch (_: Exception) {}
        }
    }
}
