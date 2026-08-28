package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FloatingIconAnimation
import com.example.data.model.FloatingIconConfig
import com.example.data.model.FloatingIconShape
import com.example.data.model.GestureAction
import com.example.data.model.GestureShortcutsConfig
import com.example.data.model.PanelAction
import com.example.data.model.SmartPanelLayoutMode
import com.example.service.RecordingStatus
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.util.DeviceCapabilities
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun FloatingSmartRecorderPanel(
    recordingStatus: RecordingStatus,
    iconConfig: FloatingIconConfig,
    panelLayoutMode: SmartPanelLayoutMode,
    customActions: List<PanelAction>,
    gestureConfig: GestureShortcutsConfig,
    isControlsLocked: Boolean,
    isMicActive: Boolean,
    isDeviceAudioActive: Boolean,
    isFacecamActive: Boolean,
    isTouchActive: Boolean,
    isPrivacyShieldActive: Boolean,
    isLaserActive: Boolean,
    isSpotlightActive: Boolean,
    isMagnifierActive: Boolean,
    isPartialActive: Boolean,
    isDrawingActive: Boolean,
    liveAudioLevel: Float,
    resolution: String,
    fps: Int,
    freeStorageFormatted: String,
    onPauseResume: () -> Unit,
    onStop: () -> Unit,
    onEmergencyStop: () -> Unit,
    onScreenshot: () -> Unit,
    onRestart: () -> Unit,
    onToggleMic: () -> Unit,
    onToggleDeviceAudio: () -> Unit,
    onToggleFacecam: () -> Unit,
    onSwitchCamera: () -> Unit,
    onToggleTouch: () -> Unit,
    onToggleLaser: () -> Unit,
    onToggleSpotlight: () -> Unit,
    onToggleMagnifier: () -> Unit,
    onTogglePrivacyShield: () -> Unit,
    onTogglePartial: () -> Unit,
    onToggleDrawing: () -> Unit,
    onToggleControlLock: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val isRecordingActive = recordingStatus is RecordingStatus.Recording
    val durationMs = (recordingStatus as? RecordingStatus.Recording)?.durationMs ?: 0L
    val isPaused = (recordingStatus as? RecordingStatus.Recording)?.isPaused ?: false

    var isPanelExpanded by remember { mutableStateOf(false) }
    var isMoreDrawerOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showStopConfirmation by remember { mutableStateOf(false) }

    // Position State in Pixels
    val animOffsetX = remember { Animatable(100f) }
    val animOffsetY = remember { Animatable(220f) }

    var isInitializedPos by remember { mutableStateOf(false) }

    // Pulsing and glow animations
    val infiniteTransition = rememberInfiniteTransition(label = "floating_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (iconConfig.animation == FloatingIconAnimation.PULSE && isRecordingActive && !isPaused) 0.88f else 1.0f,
        targetValue = if (iconConfig.animation == FloatingIconAnimation.PULSE && isRecordingActive && !isPaused) 1.12f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = if (iconConfig.animation == FloatingIconAnimation.BOUNCE && isRecordingActive) -5f else 0f,
        targetValue = if (iconConfig.animation == FloatingIconAnimation.BOUNCE && isRecordingActive) 5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Gesture Dispatcher
    fun handleGesture(action: GestureAction) {
        when (action) {
            GestureAction.PAUSE_RESUME -> onPauseResume()
            GestureAction.SCREENSHOT -> onScreenshot()
            GestureAction.OPEN_PANEL -> isPanelExpanded = !isPanelExpanded
            GestureAction.PRIVACY_SHIELD -> onTogglePrivacyShield()
            GestureAction.DRAW -> onToggleDrawing()
            GestureAction.CONTROL_LOCK -> onToggleControlLock()
            GestureAction.EMERGENCY_STOP -> onEmergencyStop()
            GestureAction.NONE -> {}
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        LaunchedEffect(screenWidthPx, screenHeightPx) {
            if (!isInitializedPos && screenWidthPx > 0 && screenHeightPx > 0) {
                val initX = (screenWidthPx * iconConfig.positionXRatio) - 40f
                val initY = (screenHeightPx * iconConfig.positionYRatio).coerceIn(100f, screenHeightPx - 200f)
                animOffsetX.snapTo(initX)
                animOffsetY.snapTo(initY)
                isInitializedPos = true
            }
        }

        // Magnetic Edge Snap Helper
        fun snapToEdge(currentX: Float, currentY: Float) {
            if (!iconConfig.edgeSnap) return
            val marginPx = with(density) { iconConfig.edgeMarginDp.dp.toPx() }
            val targetX = if (currentX < screenWidthPx / 2f) marginPx else (screenWidthPx - 110f - marginPx)
            val targetY = currentY.coerceIn(marginPx + 80f, screenHeightPx - 160f - marginPx)

            coroutineScope.launch {
                animOffsetX.animateTo(targetX, tween(240, easing = FastOutSlowInEasing))
                animOffsetY.animateTo(targetY, tween(240, easing = FastOutSlowInEasing))
            }
        }

        // 1. FLOATING RECORDER ICON BUBBLE (When panel is collapsed)
        if (!isPanelExpanded) {
            val baseShape: Shape = when (iconConfig.shape) {
                FloatingIconShape.CIRCLE -> CircleShape
                FloatingIconShape.ROUNDED_SQUARE -> RoundedCornerShape(16.dp)
                FloatingIconShape.PILL -> RoundedCornerShape(24.dp)
                FloatingIconShape.MINIMAL_DOT -> CircleShape
            }

            val iconSize = (50 * iconConfig.sizeScale).dp

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            animOffsetX.value.roundToInt(),
                            (animOffsetY.value + bounceOffset).roundToInt()
                        )
                    }
                    .scale(if (iconConfig.animation == FloatingIconAnimation.PULSE) pulseScale else 1.0f)
                    .alpha(iconConfig.opacity)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                snapToEdge(animOffsetX.value, animOffsetY.value)
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                animOffsetX.snapTo((animOffsetX.value + dragAmount.x).coerceIn(10f, screenWidthPx - 100f))
                                animOffsetY.snapTo((animOffsetY.value + dragAmount.y).coerceIn(60f, screenHeightPx - 120f))
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (isControlsLocked) {
                                    // Feedback for locked control
                                } else {
                                    isPanelExpanded = true
                                }
                            },
                            onDoubleTap = {
                                if (!isControlsLocked) handleGesture(gestureConfig.doubleTap)
                            },
                            onLongPress = {
                                if (isControlsLocked) {
                                    onToggleControlLock() // Unlock on long press
                                } else {
                                    handleGesture(gestureConfig.longPress)
                                }
                            }
                        )
                    }
                    .testTag("floating_recorder_bubble")
            ) {
                Surface(
                    shape = baseShape,
                    color = if (isControlsLocked) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.95f),
                    shadowElevation = if (iconConfig.hasShadow) 14.dp else 0.dp,
                    border = if (iconConfig.hasBorder) androidx.compose.foundation.BorderStroke(
                        width = 1.5.dp,
                        color = if (isPrivacyShieldActive) Color(0xFF10B981) else if (isControlsLocked) Color(0xFFEF4444) else Color.White.copy(alpha = 0.25f)
                    ) else null
                ) {
                    if (iconConfig.shape == FloatingIconShape.MINIMAL_DOT) {
                        // Minimal Dot Shape
                        Box(
                            modifier = Modifier
                                .size(iconSize * 0.7f)
                                .background(if (isPaused) Color(0xFFF59E0B) else StudioRecordRed),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isControlsLocked) {
                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    } else if (iconConfig.shape == FloatingIconShape.PILL) {
                        // Pill HUD Shape
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (iconConfig.showRecDot) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isPaused) Color(0xFFF59E0B) else if (isControlsLocked) Color(0xFFEF4444) else StudioRecordRed)
                                )
                            }

                            if (isControlsLocked) {
                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "LOCKED",
                                    color = Color(0xFFF87171),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                                )
                            } else {
                                if (iconConfig.showTimer) {
                                    Text(
                                        text = DeviceCapabilities.formatDuration(durationMs),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    )
                                }

                                if (isPrivacyShieldActive) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                }

                                if (isPaused) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFF59E0B).copy(alpha = 0.3f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("PAUSED", color = Color(0xFFFBBF24), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                    } else {
                        // Circle or Rounded Square
                        Box(
                            modifier = Modifier
                                .size(iconSize)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isControlsLocked) {
                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFFF87171), modifier = Modifier.size(20.dp))
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(if (isPaused) Color(0xFFF59E0B) else StudioRecordRed)
                                    )
                                    if (iconConfig.showTimer) {
                                        Text(
                                            text = DeviceCapabilities.formatDuration(durationMs).substring(3),
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. EXPANDED SMART RECORDING CONTROL PANEL (Scale + Fade Animation)
        AnimatedVisibility(
            visible = isPanelExpanded,
            enter = scaleIn(initialScale = 0.85f, animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                    fadeIn(animationSpec = tween(200)),
            exit = scaleOut(targetScale = 0.85f, animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                    fadeOut(animationSpec = tween(180)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 580.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .testTag("smart_recording_panel_card"),
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.96f),
                shadowElevation = 24.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.18f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header Bar with Realtime Metrics & Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Duration & Status Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .scale(if (isPaused) 1f else pulseScale)
                                    .clip(CircleShape)
                                    .background(if (isPaused) Color(0xFFF59E0B) else StudioRecordRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = DeviceCapabilities.formatDuration(durationMs),
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                    if (isPaused) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "PAUSED",
                                            color = Color(0xFFFBBF24),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        )
                                    }
                                }
                                Text(
                                    text = "$resolution @ ${fps}fps · $freeStorageFormatted free",
                                    color = Color.White.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                )
                            }
                        }

                        // Top Action Icons (Lock, Collapse)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onToggleControlLock,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isControlsLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = "Lock Controls",
                                    tint = if (isControlsLocked) Color(0xFFEF4444) else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { isPanelExpanded = false },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Smart Panel",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Audio Level Meter Bar
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isMicActive) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = null,
                                tint = if (isMicActive) accent.primary else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isMicActive) "Live Mic Input" else "Mic Muted",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                            )
                        }

                        // VU Level Segment Bar
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                            val activeSegments = (liveAudioLevel * 8).toInt().coerceIn(1, 8)
                            for (i in 1..8) {
                                val isLit = i <= activeSegments && isMicActive
                                val segColor = when {
                                    i > 6 -> Color(0xFFEF4444)
                                    i > 4 -> Color(0xFFF59E0B)
                                    else -> Color(0xFF10B981)
                                }
                                Box(
                                    modifier = Modifier
                                        .width(5.dp)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (isLit) segColor else Color.White.copy(alpha = 0.15f))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. ACTIONS GRID (According to layout mode)
                    val activeActionList = when (panelLayoutMode) {
                        SmartPanelLayoutMode.COMPACT -> listOf(
                            PanelAction.PAUSE_RESUME, PanelAction.STOP,
                            PanelAction.SCREENSHOT, PanelAction.MIC_TOGGLE
                        )
                        SmartPanelLayoutMode.GAMING -> listOf(
                            PanelAction.PAUSE_RESUME, PanelAction.STOP,
                            PanelAction.SCREENSHOT, PanelAction.MIC_TOGGLE,
                            PanelAction.PERFORMANCE_MODE, PanelAction.CONTROL_LOCK
                        )
                        SmartPanelLayoutMode.TUTORIAL -> listOf(
                            PanelAction.PAUSE_RESUME, PanelAction.STOP,
                            PanelAction.DRAW, PanelAction.LASER_POINTER,
                            PanelAction.SPOTLIGHT, PanelAction.TOUCH_EFFECT,
                            PanelAction.SCREENSHOT, PanelAction.CAM_TOGGLE
                        )
                        SmartPanelLayoutMode.STANDARD -> listOf(
                            PanelAction.PAUSE_RESUME, PanelAction.SCREENSHOT, PanelAction.STOP,
                            PanelAction.MIC_TOGGLE, PanelAction.CAM_TOGGLE, PanelAction.TOUCH_EFFECT,
                            PanelAction.DRAW, PanelAction.PRIVACY_SHIELD
                        )
                        SmartPanelLayoutMode.EXPANDED -> listOf(
                            PanelAction.PAUSE_RESUME, PanelAction.STOP, PanelAction.SCREENSHOT, PanelAction.RESTART,
                            PanelAction.MIC_TOGGLE, PanelAction.DEVICE_AUDIO, PanelAction.CAM_TOGGLE, PanelAction.CAM_SWITCH,
                            PanelAction.TOUCH_EFFECT, PanelAction.LASER_POINTER, PanelAction.SPOTLIGHT, PanelAction.MAGNIFIER,
                            PanelAction.DRAW, PanelAction.PRIVACY_SHIELD, PanelAction.PARTIAL_CAPTURE, PanelAction.ORIENTATION_LOCK
                        )
                        SmartPanelLayoutMode.CUSTOM -> customActions.ifEmpty {
                            listOf(
                                PanelAction.PAUSE_RESUME, PanelAction.SCREENSHOT, PanelAction.STOP,
                                PanelAction.MIC_TOGGLE, PanelAction.CAM_TOGGLE, PanelAction.DRAW
                            )
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (panelLayoutMode == SmartPanelLayoutMode.EXPANDED) 4 else 3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeActionList) { action ->
                            SmartPanelActionButton(
                                action = action,
                                isPaused = isPaused,
                                isMicActive = isMicActive,
                                isDeviceAudioActive = isDeviceAudioActive,
                                isFacecamActive = isFacecamActive,
                                isTouchActive = isTouchActive,
                                isPrivacyShieldActive = isPrivacyShieldActive,
                                isLaserActive = isLaserActive,
                                isSpotlightActive = isSpotlightActive,
                                isMagnifierActive = isMagnifierActive,
                                isPartialActive = isPartialActive,
                                isDrawingActive = isDrawingActive,
                                isControlsLocked = isControlsLocked,
                                onClick = {
                                    when (action) {
                                        PanelAction.PAUSE_RESUME -> onPauseResume()
                                        PanelAction.STOP -> {
                                            showStopConfirmation = true
                                        }
                                        PanelAction.RESTART -> onRestart()
                                        PanelAction.SCREENSHOT -> onScreenshot()
                                        PanelAction.PARTIAL_CAPTURE -> onTogglePartial()
                                        PanelAction.MIC_TOGGLE -> onToggleMic()
                                        PanelAction.DEVICE_AUDIO -> onToggleDeviceAudio()
                                        PanelAction.CAM_TOGGLE -> onToggleFacecam()
                                        PanelAction.CAM_SWITCH -> onSwitchCamera()
                                        PanelAction.TOUCH_EFFECT -> onToggleTouch()
                                        PanelAction.LASER_POINTER -> onToggleLaser()
                                        PanelAction.SPOTLIGHT -> onToggleSpotlight()
                                        PanelAction.MAGNIFIER -> onToggleMagnifier()
                                        PanelAction.DRAW -> onToggleDrawing()
                                        PanelAction.PRIVACY_SHIELD -> onTogglePrivacyShield()
                                        PanelAction.CONTROL_LOCK -> onToggleControlLock()
                                        PanelAction.ORIENTATION_LOCK -> {}
                                        PanelAction.PERFORMANCE_MODE -> {}
                                        PanelAction.BRIGHTNESS -> {}
                                        PanelAction.LIBRARY -> onOpenLibrary()
                                        PanelAction.SETTINGS -> onOpenSettings()
                                    }
                                }
                            )
                        }
                    }

                    // 4. SECONDARY "MORE TOOLS" DRAWER
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { isMoreDrawerOpen = !isMoreDrawerOpen },
                        color = Color.White.copy(alpha = 0.07f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = accent.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isMoreDrawerOpen) "Hide Advanced Tools Matrix" else "More Studio Tools & Matrix Search",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                )
                            }
                            Icon(
                                imageVector = if (isMoreDrawerOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    AnimatedVisibility(visible = isMoreDrawerOpen) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            // Search Box
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search tools (e.g., blur, spotlight, laser)", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = accent.primary,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    cursorColor = accent.primary
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Filtered tools matrix
                            val allTools = PanelAction.values().filter {
                                searchQuery.isBlank() ||
                                        it.label.contains(searchQuery, ignoreCase = true) ||
                                        it.category.contains(searchQuery, ignoreCase = true)
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 140.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(allTools.size) { index ->
                                    val tool = allTools[index]
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                when (tool) {
                                                    PanelAction.PAUSE_RESUME -> onPauseResume()
                                                    PanelAction.STOP -> showStopConfirmation = true
                                                    PanelAction.RESTART -> onRestart()
                                                    PanelAction.SCREENSHOT -> onScreenshot()
                                                    PanelAction.PARTIAL_CAPTURE -> onTogglePartial()
                                                    PanelAction.MIC_TOGGLE -> onToggleMic()
                                                    PanelAction.DEVICE_AUDIO -> onToggleDeviceAudio()
                                                    PanelAction.CAM_TOGGLE -> onToggleFacecam()
                                                    PanelAction.CAM_SWITCH -> onSwitchCamera()
                                                    PanelAction.TOUCH_EFFECT -> onToggleTouch()
                                                    PanelAction.LASER_POINTER -> onToggleLaser()
                                                    PanelAction.SPOTLIGHT -> onToggleSpotlight()
                                                    PanelAction.MAGNIFIER -> onToggleMagnifier()
                                                    PanelAction.DRAW -> onToggleDrawing()
                                                    PanelAction.PRIVACY_SHIELD -> onTogglePrivacyShield()
                                                    PanelAction.CONTROL_LOCK -> onToggleControlLock()
                                                    PanelAction.ORIENTATION_LOCK -> {}
                                                    PanelAction.PERFORMANCE_MODE -> {}
                                                    PanelAction.BRIGHTNESS -> {}
                                                    PanelAction.LIBRARY -> onOpenLibrary()
                                                    PanelAction.SETTINGS -> onOpenSettings()
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(tool.defaultIcon, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(tool.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }
                                        Text(tool.category, color = accent.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // 5. EMERGENCY STOP & SAFE FINALIZE FOOTER
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onEmergencyStop,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Emergency Stop & Save", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = { isPanelExpanded = false },
                            modifier = Modifier
                                .weight(0.6f)
                                .height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                        ) {
                            Text("Minimize", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Stop Recording Confirmation Dialog
    if (showStopConfirmation) {
        AlertDialog(
            onDismissRequest = { showStopConfirmation = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stop, contentDescription = null, tint = StudioRecordRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop Screen Recording?", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("Your recording (${DeviceCapabilities.formatDuration(durationMs)}) will be saved locally to your device and added to your Studio Library.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showStopConfirmation = false
                        isPanelExpanded = false
                        onStop()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRecordRed)
                ) {
                    Text("Stop & Save Video", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopConfirmation = false }) {
                    Text("Keep Recording")
                }
            }
        )
    }
}

private data class SmartActionInfo(
    val icon: ImageVector,
    val label: String,
    val isActive: Boolean,
    val activeColor: Color
)

@Composable
private fun SmartPanelActionButton(
    action: PanelAction,
    isPaused: Boolean,
    isMicActive: Boolean,
    isDeviceAudioActive: Boolean,
    isFacecamActive: Boolean,
    isTouchActive: Boolean,
    isPrivacyShieldActive: Boolean,
    isLaserActive: Boolean,
    isSpotlightActive: Boolean,
    isMagnifierActive: Boolean,
    isPartialActive: Boolean,
    isDrawingActive: Boolean,
    isControlsLocked: Boolean,
    onClick: () -> Unit
) {
    val accent = LocalAccentTheme.current

    val info: SmartActionInfo = when (action) {
        PanelAction.PAUSE_RESUME -> {
            if (isPaused) SmartActionInfo(Icons.Default.PlayArrow, "Resume", true, Color(0xFFF59E0B))
            else SmartActionInfo(Icons.Default.Pause, "Pause", false, accent.primary)
        }
        PanelAction.STOP -> SmartActionInfo(Icons.Default.Stop, "Stop", true, StudioRecordRed)
        PanelAction.RESTART -> SmartActionInfo(Icons.Default.Refresh, "Restart", false, accent.primary)
        PanelAction.SCREENSHOT -> SmartActionInfo(Icons.Default.CameraAlt, "Snapshot", false, accent.primary)
        PanelAction.PARTIAL_CAPTURE -> SmartActionInfo(Icons.Default.AspectRatio, "Partial", isPartialActive, accent.primary)
        PanelAction.MIC_TOGGLE -> {
            if (isMicActive) SmartActionInfo(Icons.Default.Mic, "Mic On", true, accent.primary)
            else SmartActionInfo(Icons.Default.MicOff, "Mic Off", false, Color.Gray)
        }
        PanelAction.DEVICE_AUDIO -> SmartActionInfo(Icons.Default.Headset, "Device Audio", isDeviceAudioActive, accent.primary)
        PanelAction.CAM_TOGGLE -> {
            if (isFacecamActive) SmartActionInfo(Icons.Default.Videocam, "Facecam", true, accent.primary)
            else SmartActionInfo(Icons.Default.VideocamOff, "Facecam Off", false, Color.Gray)
        }
        PanelAction.CAM_SWITCH -> SmartActionInfo(Icons.Default.FlipCameraAndroid, "Switch Lens", false, accent.primary)
        PanelAction.TOUCH_EFFECT -> SmartActionInfo(Icons.Default.TouchApp, "Touch Ripple", isTouchActive, accent.primary)
        PanelAction.LASER_POINTER -> SmartActionInfo(Icons.Default.Flare, "Laser Pointer", isLaserActive, Color(0xFFFF2A6D))
        PanelAction.SPOTLIGHT -> SmartActionInfo(Icons.Default.Highlight, "Spotlight", isSpotlightActive, Color(0xFFFBBF24))
        PanelAction.MAGNIFIER -> SmartActionInfo(Icons.Default.ZoomIn, "Magnifier", isMagnifierActive, accent.primary)
        PanelAction.DRAW -> SmartActionInfo(Icons.Default.Draw, "Screen Draw", isDrawingActive, accent.primary)
        PanelAction.PRIVACY_SHIELD -> SmartActionInfo(Icons.Default.Security, "Privacy Shield", isPrivacyShieldActive, Color(0xFF10B981))
        PanelAction.CONTROL_LOCK -> SmartActionInfo(if (isControlsLocked) Icons.Default.Lock else Icons.Default.LockOpen, if (isControlsLocked) "Locked" else "Lock UI", isControlsLocked, Color(0xFFEF4444))
        PanelAction.ORIENTATION_LOCK -> SmartActionInfo(Icons.Default.ScreenRotation, "Orientation", false, accent.primary)
        PanelAction.PERFORMANCE_MODE -> SmartActionInfo(Icons.Default.Speed, "Performance", false, accent.primary)
        PanelAction.BRIGHTNESS -> SmartActionInfo(Icons.Default.Tune, "Brightness", false, accent.primary)
        PanelAction.LIBRARY -> SmartActionInfo(Icons.Default.VideoLibrary, "Library", false, accent.primary)
        PanelAction.SETTINGS -> SmartActionInfo(Icons.Default.Settings, "Settings", false, accent.primary)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("smart_panel_btn_${action.id}"),
        shape = RoundedCornerShape(16.dp),
        color = if (info.isActive) info.activeColor.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (info.isActive) 1.5.dp else 1.dp,
            color = if (info.isActive) info.activeColor.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = info.icon,
                contentDescription = info.label,
                tint = if (info.isActive) info.activeColor else Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = info.label,
                color = if (info.isActive) Color.White else Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (info.isActive) FontWeight.Bold else FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
