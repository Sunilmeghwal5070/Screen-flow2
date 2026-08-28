package com.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.model.RecordingEntity
import com.example.service.QuickRecordTileService
import com.example.service.RecordingStatus
import com.example.ui.components.FloatingSmartRecorderPanel
import com.example.ui.components.LaserAndSpotlightOverlay
import com.example.ui.components.MagnifierOverlay
import com.example.ui.components.OverlayPermissionPromptDialog
import com.example.ui.components.PartialScreenSelectorOverlay
import com.example.ui.components.PrivacyShieldOverlay
import com.example.ui.components.ScreenAnnotationOverlay
import com.example.ui.components.StudioBottomNav
import com.example.ui.components.StudioTopBar
import com.example.ui.components.TouchAndGestureVisualizer
import com.example.ui.screens.CreatePresetDialog
import com.example.ui.screens.FloatingStudioScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.LowStorageWarningDialog
import com.example.ui.screens.PermissionsExplainerDialog
import com.example.ui.screens.RecordScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartWizardDialog
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.VideoPlayerAndEditorDialog
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.ScreenFlowTheme
import com.example.ui.viewmodel.ActiveModal
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab
import com.example.util.DeviceCapabilities

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            viewModel.startRecordingService(this, result.resultCode, result.data!!)
        } else {
            Toast.makeText(this, "Screen recording permission was not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val micGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (!micGranted) {
            Toast.makeText(this, "Microphone permission is needed for voice narration.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestInitialPermissions()
        handleIntent(intent)

        setContent {
            val currentAccent by viewModel.currentAccent.collectAsState()
            val currentTab by viewModel.currentTab.collectAsState()
            val activeModal by viewModel.activeModal.collectAsState()

            val recordingStatus by viewModel.recordingStatus.collectAsState()
            val liveAudioLevel by viewModel.liveAudioAmplitude.collectAsState()

            val resolution by viewModel.resolution.collectAsState()
            val fps by viewModel.fps.collectAsState()
            val bitrateMbps by viewModel.bitrateMbps.collectAsState()
            val audioSource by viewModel.audioSource.collectAsState()
            val orientation by viewModel.orientation.collectAsState()
            val countdownSeconds by viewModel.countdownSeconds.collectAsState()
            val autoStopMinutes by viewModel.autoStopMinutes.collectAsState()

            val isTouchEnabled by viewModel.touchEnabled.collectAsState()
            val touchStyle by viewModel.touchStyle.collectAsState()
            val touchColor by viewModel.touchColor.collectAsState()
            val touchSizeDp by viewModel.touchSizeDp.collectAsState()

            val isFacecamEnabled by viewModel.facecamEnabled.collectAsState()
            val facecamShape by viewModel.facecamShape.collectAsState()
            val facecamPosition by viewModel.facecamPosition.collectAsState()
            val facecamSizeDp by viewModel.facecamSizeDp.collectAsState()
            val isFrontCamera by viewModel.isFrontCamera.collectAsState()

            val isWatermarkEnabled by viewModel.watermarkEnabled.collectAsState()
            val watermarkText by viewModel.watermarkText.collectAsState()

            val allRecordings by viewModel.allRecordings.collectAsState()
            val recentRecordings by viewModel.recentRecordings.collectAsState()
            val allPresets by viewModel.allPresets.collectAsState()
            val appStorageBytes by viewModel.appStorageBytes.collectAsState()
            val screenshotStorageBytes by viewModel.screenshotStorageBytes.collectAsState()

            val libraryFilter by viewModel.libraryFilter.collectAsState()
            val librarySort by viewModel.librarySort.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val exportProgress by viewModel.exportProgress.collectAsState()

            // Floating Smart Panel & Overlays State
            val iconConfig by viewModel.floatingIconConfig.collectAsState()
            val panelLayoutMode by viewModel.smartPanelLayoutMode.collectAsState()
            val customPanelActions by viewModel.customPanelActions.collectAsState()
            val gestureConfig by viewModel.gestureShortcutsConfig.collectAsState()
            val isControlsLocked by viewModel.isControlsLocked.collectAsState()
            val isDeviceAudioActive by viewModel.isDeviceAudioActive.collectAsState()

            val privacyZones by viewModel.privacyZones.collectAsState()
            val isPrivacyShieldActive by viewModel.isPrivacyShieldActive.collectAsState()
            val isPrivacyShieldEditMode by viewModel.isPrivacyShieldEditMode.collectAsState()

            val partialArea by viewModel.partialArea.collectAsState()
            val isPartialSelectorActive by viewModel.isPartialSelectorActive.collectAsState()

            val laserConfig by viewModel.laserConfig.collectAsState()
            val spotlightConfig by viewModel.spotlightConfig.collectAsState()
            val magnifierConfig by viewModel.magnifierConfig.collectAsState()

            var isAnnotationWorkbenchOpen by remember { mutableStateOf(false) }

            val isRecordingActive = recordingStatus is RecordingStatus.Recording
            val freeBytes = DeviceCapabilities.getFreeStorageBytes(this)
            val freeFormatted = DeviceCapabilities.formatBytes(freeBytes)

            ScreenFlowTheme(accentTheme = currentAccent) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        StudioTopBar(
                            isRecording = isRecordingActive,
                            onWizardClick = { viewModel.setModal(ActiveModal.Wizard) },
                            onPaletteClick = { viewModel.setTab(NavigationTab.SETTINGS) }
                        )
                    },
                    bottomBar = {
                        StudioBottomNav(
                            selectedTab = currentTab,
                            onTabSelected = { viewModel.setTab(it) },
                            isRecording = isRecordingActive
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_switch"
                        ) { tab ->
                            when (tab) {
                                NavigationTab.HOME -> {
                                    HomeScreen(
                                        recordingStatus = recordingStatus,
                                        resolution = resolution,
                                        fps = fps,
                                        audioSource = audioSource,
                                        orientation = orientation,
                                        bitrateMbps = bitrateMbps,
                                        liveAudioLevel = liveAudioLevel,
                                        presets = allPresets,
                                        recentRecordings = recentRecordings,
                                        appStorageBytes = appStorageBytes ?: 0L,
                                        screenshotStorageBytes = screenshotStorageBytes ?: 0L,
                                        onStartRecording = { startRecordingWithOverlayCheck() },
                                        onStopRecording = { viewModel.stopRecording(this@MainActivity) },
                                        onQuickRecord = { startRecordingWithOverlayCheck() },
                                        onTakeScreenshot = { viewModel.takeScreenshot(this@MainActivity) },
                                        onSelectPreset = { viewModel.applyPreset(it) },
                                        onOpenWizard = { viewModel.setModal(ActiveModal.Wizard) },
                                        onOpenDrawingWorkbench = { isAnnotationWorkbenchOpen = true },
                                        onOpenStorageManager = { viewModel.setModal(ActiveModal.StorageManager) },
                                        onNavigateTab = { viewModel.setTab(it) },
                                        onPlayRecording = { viewModel.setModal(ActiveModal.VideoPlayer(it)) },
                                        onEditRecording = { viewModel.setModal(ActiveModal.VideoEditor(it)) },
                                        onShareRecording = { viewModel.shareRecording(this@MainActivity, it) }
                                    )
                                }
                                NavigationTab.RECORD -> {
                                    RecordScreen(
                                        recordingStatus = recordingStatus,
                                        resolution = resolution,
                                        fps = fps,
                                        bitrateMbps = bitrateMbps,
                                        audioSource = audioSource,
                                        orientation = orientation,
                                        countdownSeconds = countdownSeconds,
                                        autoStopMinutes = autoStopMinutes,
                                        isTouchEnabled = isTouchEnabled,
                                        touchStyle = touchStyle,
                                        touchSizeDp = touchSizeDp,
                                        isFacecamEnabled = isFacecamEnabled,
                                        facecamShape = facecamShape,
                                        facecamPosition = facecamPosition,
                                        facecamSizeDp = facecamSizeDp,
                                        isFrontCamera = isFrontCamera,
                                        isWatermarkEnabled = isWatermarkEnabled,
                                        watermarkText = watermarkText,
                                        liveAudioLevel = liveAudioLevel,
                                        onResolutionChange = { res, w, h, br -> viewModel.setResolution(res, w, h, br) },
                                        onFpsChange = { viewModel.setFps(it) },
                                        onBitrateChange = { viewModel.setBitrate(it) },
                                        onAudioSourceChange = { viewModel.setAudioSource(it) },
                                        onOrientationChange = { viewModel.setOrientation(it) },
                                        onCountdownChange = { viewModel.setCountdown(it) },
                                        onAutoStopChange = { viewModel.setAutoStop(it) },
                                        onToggleTouch = { viewModel.toggleTouch(it) },
                                        onSetTouchStyle = { viewModel.setTouchStyle(it) },
                                        onSetTouchSize = { viewModel.setTouchSize(it) },
                                        onToggleFacecam = { viewModel.toggleFacecam(it) },
                                        onSetFacecamShape = { viewModel.setFacecamShape(it) },
                                        onSetFacecamPosition = { viewModel.setFacecamPosition(it) },
                                        onSetFacecamSize = { viewModel.setFacecamSize(it) },
                                        onToggleCameraLens = { viewModel.toggleCameraLens() },
                                        onToggleWatermark = { viewModel.toggleWatermark(it) },
                                        onSetWatermarkText = { viewModel.setWatermarkText(it) },
                                        onStartRecording = { startRecordingWithOverlayCheck() },
                                        onStopRecording = { viewModel.stopRecording(this@MainActivity) },
                                        onSaveCustomPreset = { viewModel.setModal(ActiveModal.CreatePreset) }
                                    )
                                }
                                NavigationTab.LIBRARY -> {
                                    LibraryScreen(
                                        recordings = allRecordings,
                                        selectedFilter = libraryFilter,
                                        selectedSort = librarySort,
                                        searchQuery = searchQuery,
                                        onFilterSelect = { viewModel.setLibraryFilter(it) },
                                        onSortSelect = { viewModel.setLibrarySort(it) },
                                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                        onPlayRecording = { viewModel.setModal(ActiveModal.VideoPlayer(it)) },
                                        onEditRecording = { viewModel.setModal(ActiveModal.VideoEditor(it)) },
                                        onShareRecording = { viewModel.shareRecording(this@MainActivity, it) },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                                        onRenameRecording = { rec, title -> viewModel.renameRecording(rec, title) },
                                        onDeleteRecording = { viewModel.deleteRecording(it) },
                                        onStartRecording = { startRecordingWithOverlayCheck() }
                                    )
                                }
                                NavigationTab.TOOLS -> {
                                    ToolsScreen(
                                        liveAudioLevel = liveAudioLevel,
                                        isFacecamEnabled = isFacecamEnabled,
                                        facecamShape = facecamShape,
                                        isFrontCamera = isFrontCamera,
                                        touchStyle = touchStyle,
                                        watermarkText = watermarkText,
                                        appStorageBytes = appStorageBytes ?: 0L,
                                        screenshotStorageBytes = screenshotStorageBytes ?: 0L,
                                        isLaserActive = laserConfig.isEnabled,
                                        isSpotlightActive = spotlightConfig.isEnabled,
                                        isMagnifierActive = magnifierConfig.isEnabled,
                                        isPrivacyActive = isPrivacyShieldActive,
                                        onSetFacecamShape = { viewModel.setFacecamShape(it) },
                                        onToggleCameraLens = { viewModel.toggleCameraLens() },
                                        onSetTouchStyle = { viewModel.setTouchStyle(it) },
                                        onSetWatermarkText = { viewModel.setWatermarkText(it) },
                                        onOpenDrawingWorkbench = { isAnnotationWorkbenchOpen = true },
                                        onOpenFloatingStudio = { viewModel.setModal(ActiveModal.FloatingStudio) },
                                        onOpenPrivacyStudio = {
                                            viewModel.setPrivacyShieldEditMode(true)
                                            viewModel.setModal(ActiveModal.PrivacyShieldStudio)
                                        },
                                        onOpenPartialSelector = {
                                            viewModel.togglePartialSelector()
                                        },
                                        onToggleLaser = { viewModel.toggleLaserPointer() },
                                        onToggleSpotlight = { viewModel.toggleSpotlight() },
                                        onToggleMagnifier = { viewModel.toggleMagnifier() },
                                        onClearCache = { viewModel.clearCache(this@MainActivity) }
                                    )
                                }
                                NavigationTab.SETTINGS -> {
                                    SettingsScreen(
                                        currentAccent = currentAccent,
                                        resolution = resolution,
                                        fps = fps,
                                        audioSource = audioSource,
                                        countdownSeconds = countdownSeconds,
                                        isFacecamEnabled = isFacecamEnabled,
                                        onSelectAccent = { viewModel.setAccentTheme(it) },
                                        onResolutionChange = { res, w, h, br -> viewModel.setResolution(res, w, h, br) },
                                        onFpsChange = { viewModel.setFps(it) },
                                        onAudioSourceChange = { viewModel.setAudioSource(it) },
                                        onCountdownChange = { viewModel.setCountdown(it) },
                                        onToggleFacecam = { viewModel.toggleFacecam(it) },
                                        onShowPermissions = { viewModel.setModal(ActiveModal.PermissionsExplainer) }
                                    )
                                }
                            }
                        }

                        // Privacy Shield Overlay (When active or during studio config)
                        if (isPrivacyShieldActive || isPrivacyShieldEditMode || activeModal is ActiveModal.PrivacyShieldStudio) {
                            PrivacyShieldOverlay(
                                zones = privacyZones,
                                isEditMode = isPrivacyShieldEditMode || activeModal is ActiveModal.PrivacyShieldStudio,
                                onAddZone = { viewModel.addPrivacyZone(it) },
                                onUpdateZone = { viewModel.updatePrivacyZone(it) },
                                onDeleteZone = { viewModel.deletePrivacyZone(it) },
                                onCloseEditMode = {
                                    viewModel.setPrivacyShieldEditMode(false)
                                    if (activeModal is ActiveModal.PrivacyShieldStudio) {
                                        viewModel.setModal(ActiveModal.None)
                                    }
                                }
                            )
                        }

                        // Laser Pointer & Spotlight Overlay
                        if (laserConfig.isEnabled || spotlightConfig.isEnabled) {
                            LaserAndSpotlightOverlay(
                                laserConfig = laserConfig,
                                spotlightConfig = spotlightConfig,
                                onCloseLaser = { viewModel.toggleLaserPointer() },
                                onCloseSpotlight = { viewModel.toggleSpotlight() }
                            )
                        }

                        // Live Magnifier Loupe Overlay
                        if (magnifierConfig.isEnabled) {
                            MagnifierOverlay(
                                config = magnifierConfig,
                                onZoomChange = { viewModel.updateMagnifierConfig(magnifierConfig.copy(zoomLevel = it)) },
                                onClose = { viewModel.toggleMagnifier() }
                            )
                        }

                        // Partial Screen Crop Selector Overlay
                        if (isPartialSelectorActive || activeModal is ActiveModal.PartialSelector) {
                            PartialScreenSelectorOverlay(
                                partialArea = partialArea,
                                onUpdateArea = { viewModel.updatePartialArea(it) },
                                onStartPartialRecord = {
                                    viewModel.togglePartialSelector()
                                    if (activeModal is ActiveModal.PartialSelector) {
                                        viewModel.setModal(ActiveModal.None)
                                    }
                                    initiateScreenRecording()
                                },
                                onClose = {
                                    viewModel.togglePartialSelector()
                                    if (activeModal is ActiveModal.PartialSelector) {
                                        viewModel.setModal(ActiveModal.None)
                                    }
                                }
                            )
                        }

                        // Screen Annotation Markup Canvas
                        if (isAnnotationWorkbenchOpen) {
                            ScreenAnnotationOverlay(
                                onClose = { isAnnotationWorkbenchOpen = false }
                            )
                        }

                        // Original Floating Smart Recorder Panel (Active during recording or test mode)
                        if (isRecordingActive) {
                            FloatingSmartRecorderPanel(
                                recordingStatus = recordingStatus,
                                iconConfig = iconConfig,
                                panelLayoutMode = panelLayoutMode,
                                customActions = customPanelActions,
                                gestureConfig = gestureConfig,
                                isControlsLocked = isControlsLocked,
                                isMicActive = audioSource != "Device Audio Only",
                                isDeviceAudioActive = isDeviceAudioActive,
                                isFacecamActive = isFacecamEnabled,
                                isTouchActive = isTouchEnabled,
                                isPrivacyShieldActive = isPrivacyShieldActive,
                                isLaserActive = laserConfig.isEnabled,
                                isSpotlightActive = spotlightConfig.isEnabled,
                                isMagnifierActive = magnifierConfig.isEnabled,
                                isPartialActive = isPartialSelectorActive,
                                isDrawingActive = isAnnotationWorkbenchOpen,
                                liveAudioLevel = liveAudioLevel,
                                resolution = resolution,
                                fps = fps,
                                freeStorageFormatted = freeFormatted,
                                onPauseResume = {
                                    val recStatus = recordingStatus as? RecordingStatus.Recording
                                    if (recStatus?.isPaused == true) viewModel.resumeRecording(this@MainActivity)
                                    else viewModel.pauseRecording(this@MainActivity)
                                },
                                onStop = { viewModel.stopRecording(this@MainActivity) },
                                onEmergencyStop = {
                                    viewModel.stopRecording(this@MainActivity)
                                    Toast.makeText(this@MainActivity, "Recording finalized and safely saved!", Toast.LENGTH_SHORT).show()
                                },
                                onScreenshot = { viewModel.takeScreenshot(this@MainActivity) },
                                onRestart = {
                                    viewModel.stopRecording(this@MainActivity)
                                    initiateScreenRecording()
                                },
                                onToggleMic = {
                                    val newSource = if (audioSource == "Mute") "Microphone Only" else "Mute"
                                    viewModel.setAudioSource(newSource)
                                },
                                onToggleDeviceAudio = { viewModel.toggleDeviceAudio() },
                                onToggleFacecam = { viewModel.toggleFacecam(!isFacecamEnabled) },
                                onSwitchCamera = { viewModel.toggleCameraLens() },
                                onToggleTouch = { viewModel.toggleTouch(!isTouchEnabled) },
                                onToggleLaser = { viewModel.toggleLaserPointer() },
                                onToggleSpotlight = { viewModel.toggleSpotlight() },
                                onToggleMagnifier = { viewModel.toggleMagnifier() },
                                onTogglePrivacyShield = { viewModel.togglePrivacyShield() },
                                onTogglePartial = { viewModel.togglePartialSelector() },
                                onToggleDrawing = { isAnnotationWorkbenchOpen = !isAnnotationWorkbenchOpen },
                                onToggleControlLock = { viewModel.toggleControlLock() },
                                onOpenSettings = { viewModel.setTab(NavigationTab.SETTINGS) },
                                onOpenLibrary = { viewModel.setTab(NavigationTab.LIBRARY) }
                            )
                        }
                    }
                }

                // Active Modals & Dialogs
                when (val modal = activeModal) {
                    is ActiveModal.FloatingStudio -> {
                        FloatingStudioScreen(
                            iconConfig = iconConfig,
                            panelLayoutMode = panelLayoutMode,
                            customActions = customPanelActions,
                            gestureConfig = gestureConfig,
                            onUpdateIconConfig = { viewModel.updateFloatingIconConfig(it) },
                            onUpdatePanelMode = { viewModel.setSmartPanelLayoutMode(it) },
                            onToggleCustomAction = { viewModel.toggleCustomPanelAction(it) },
                            onUpdateGestureConfig = { viewModel.updateGestureShortcuts(it) },
                            onClose = { viewModel.setModal(ActiveModal.None) },
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                    is ActiveModal.VideoPlayer -> {
                        VideoPlayerAndEditorDialog(
                            recording = modal.recording,
                            isEditorMode = false,
                            onClose = { viewModel.setModal(ActiveModal.None) },
                            onShare = { viewModel.shareRecording(this@MainActivity, modal.recording) },
                            onExportEdited = { _, _, _, _, _, _, _ -> }
                        )
                    }
                    is ActiveModal.VideoEditor -> {
                        VideoPlayerAndEditorDialog(
                            recording = modal.recording,
                            isEditorMode = true,
                            exportProgress = exportProgress,
                            onClose = { viewModel.setModal(ActiveModal.None) },
                            onShare = { viewModel.shareRecording(this@MainActivity, modal.recording) },
                            onExportEdited = { orig, start, end, spd, rot, mute, wm ->
                                viewModel.exportEditedVideo(orig, start, end, spd, rot, mute, wm) {
                                    viewModel.setModal(ActiveModal.None)
                                    Toast.makeText(this@MainActivity, "Video exported successfully!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    is ActiveModal.Wizard -> {
                        SmartWizardDialog(
                            onApplySettings = { res, w, h, f, b, a, t, fc ->
                                viewModel.setResolution(res, w, h, b)
                                viewModel.setFps(f)
                                viewModel.setAudioSource(a)
                                viewModel.toggleTouch(t)
                                viewModel.toggleFacecam(fc)
                                Toast.makeText(this@MainActivity, "Preset '$res @ ${f}fps' applied!", Toast.LENGTH_SHORT).show()
                            },
                            onDismiss = { viewModel.setModal(ActiveModal.None) }
                        )
                    }
                    is ActiveModal.CreatePreset -> {
                        CreatePresetDialog(
                            onSave = { name, desc -> viewModel.saveNewPreset(name, desc) },
                            onDismiss = { viewModel.setModal(ActiveModal.None) }
                        )
                    }
                    is ActiveModal.PermissionsExplainer -> {
                        PermissionsExplainerDialog(
                            onDismiss = { viewModel.setModal(ActiveModal.None) }
                        )
                    }
                    is ActiveModal.LowStorageWarning -> {
                        LowStorageWarningDialog(
                            onDismiss = { viewModel.setModal(ActiveModal.None) }
                        )
                    }
                    is ActiveModal.OverlayPermissionPrompt -> {
                        OverlayPermissionPromptDialog(
                            onContinueRecording = {
                                viewModel.setModal(ActiveModal.None)
                                initiateScreenRecording()
                            },
                            onDismiss = {
                                viewModel.setModal(ActiveModal.None)
                            }
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(QuickRecordTileService.EXTRA_ACTION_QUICK_RECORD, false) == true) {
            startRecordingWithOverlayCheck()
        }
    }

    private fun startRecordingWithOverlayCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            viewModel.setModal(ActiveModal.OverlayPermissionPrompt)
        } else {
            initiateScreenRecording()
        }
    }

    private fun requestInitialPermissions() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val notGranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            permissionsLauncher.launch(notGranted.toTypedArray())
        }
    }

    private fun initiateScreenRecording() {
        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(projectionManager.createScreenCaptureIntent())
    }
}
