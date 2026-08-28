package com.example.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Flare

enum class FloatingIconShape(val label: String) {
    CIRCLE("Circle"),
    ROUNDED_SQUARE("Rounded Square"),
    PILL("Pill HUD"),
    MINIMAL_DOT("Minimal Dot")
}

enum class FloatingIconAnimation(val label: String) {
    PULSE("Pulse"),
    GLOW("Glow"),
    BOUNCE("Bounce"),
    NONE("Static (Off)")
}

data class FloatingIconConfig(
    val shape: FloatingIconShape = FloatingIconShape.PILL,
    val sizeScale: Float = 1.0f, // 0.8f (Small), 1.0f (Medium), 1.25f (Large)
    val opacity: Float = 0.92f,
    val hasBorder: Boolean = true,
    val hasShadow: Boolean = true,
    val showTimer: Boolean = true,
    val showRecDot: Boolean = true,
    val animation: FloatingIconAnimation = FloatingIconAnimation.PULSE,
    val edgeSnap: Boolean = true,
    val edgeMarginDp: Int = 12,
    val autoHideSeconds: Int = 0, // 0 = Always visible, 3, 5, 10
    val rememberPosition: Boolean = true,
    val positionXRatio: Float = 0.5f,
    val positionYRatio: Float = 0.08f
)

enum class SmartPanelLayoutMode(val title: String, val description: String) {
    COMPACT("Compact (4 tools)", "Minimal footprint for clean screen viewing"),
    STANDARD("Standard (8 tools)", "Balanced control center with key recording tools"),
    EXPANDED("Expanded (12+ tools)", "Full creator console with all live studio features"),
    GAMING("Gaming Mode", "Low-profile HUD with FPS, Mic, Screenshot, and Pause"),
    TUTORIAL("Tutorial Mode", "Equipped for teaching: Draw, Laser, Spotlight, Touch, Mic"),
    CUSTOM("Custom Layout", "Fully personalized action grid tailored by you")
}

enum class PanelAction(
    val id: String,
    val label: String,
    val category: String,
    val defaultIcon: ImageVector
) {
    PAUSE_RESUME("pause_resume", "Pause / Resume", "Recording", Icons.Default.Pause),
    STOP("stop", "Stop Recording", "Recording", Icons.Default.Stop),
    RESTART("restart", "Restart Clip", "Recording", Icons.Default.Refresh),
    SCREENSHOT("screenshot", "Instant Screenshot", "Capture", Icons.Default.CameraAlt),
    PARTIAL_CAPTURE("partial_capture", "Partial Screen", "Capture", Icons.Default.AspectRatio),
    
    MIC_TOGGLE("mic_toggle", "Microphone", "Audio", Icons.Default.Mic),
    DEVICE_AUDIO("device_audio", "Device Audio", "Audio", Icons.Default.Headset),
    
    CAM_TOGGLE("cam_toggle", "Facecam PiP", "Camera", Icons.Default.Videocam),
    CAM_SWITCH("cam_switch", "Switch Lens", "Camera", Icons.Default.FlipCameraAndroid),
    
    TOUCH_EFFECT("touch_effect", "Touch Ripple", "Visual", Icons.Default.TouchApp),
    LASER_POINTER("laser_pointer", "Laser Pointer", "Visual", Icons.Default.Flare),
    SPOTLIGHT("spotlight", "Screen Spotlight", "Visual", Icons.Default.Highlight),
    MAGNIFIER("magnifier", "Live Magnifier", "Visual", Icons.Default.ZoomIn),
    
    DRAW("draw", "Screen Markup", "Annotation", Icons.Default.Draw),
    
    PRIVACY_SHIELD("privacy_shield", "Privacy Shield", "Privacy", Icons.Default.Security),
    
    CONTROL_LOCK("control_lock", "Control Lock", "Control", Icons.Default.Lock),
    ORIENTATION_LOCK("orientation_lock", "Orientation", "System", Icons.Default.ScreenRotation),
    PERFORMANCE_MODE("performance_mode", "Performance", "System", Icons.Default.Speed),
    BRIGHTNESS("brightness", "Brightness", "System", Icons.Default.BrightnessMedium),
    LIBRARY("library", "Media Library", "Navigation", Icons.Default.VideoLibrary),
    SETTINGS("settings", "Settings", "Navigation", Icons.Default.Settings)
}

enum class GestureAction(val label: String) {
    NONE("Do Nothing"),
    PAUSE_RESUME("Pause / Resume"),
    SCREENSHOT("Take Screenshot"),
    OPEN_PANEL("Open Smart Panel"),
    PRIVACY_SHIELD("Toggle Privacy Shield"),
    DRAW("Open Screen Draw"),
    CONTROL_LOCK("Toggle Control Lock"),
    EMERGENCY_STOP("Emergency Stop")
}

data class GestureShortcutsConfig(
    val doubleTap: GestureAction = GestureAction.PAUSE_RESUME,
    val longPress: GestureAction = GestureAction.OPEN_PANEL,
    val swipeUp: GestureAction = GestureAction.SCREENSHOT,
    val swipeDown: GestureAction = GestureAction.PRIVACY_SHIELD
)

enum class PrivacyZoneType(val label: String) {
    BLUR("Gaussian Blur"),
    PIXELATE("Pixelate Mosaic"),
    SOLID_COVER("Solid Color Shield")
}

data class PrivacyZone(
    val id: String,
    val x: Float, // Normalized 0f..1f
    val y: Float,
    val width: Float,
    val height: Float,
    val type: PrivacyZoneType = PrivacyZoneType.BLUR,
    val intensity: Float = 0.8f,
    val opacity: Float = 0.95f,
    val label: String = "Privacy Zone"
)

enum class PartialAspectRatio(val label: String, val ratio: Float?) {
    FREE("Freeform", null),
    RATIO_16_9("16 : 9", 16f / 9f),
    RATIO_9_16("9 : 16", 9f / 16f),
    RATIO_1_1("1 : 1 Square", 1f),
    RATIO_4_3("4 : 3", 4f / 3f)
}

data class PartialRecordingArea(
    val isEnabled: Boolean = false,
    val xRatio: Float = 0.05f,
    val yRatio: Float = 0.15f,
    val widthRatio: Float = 0.90f,
    val heightRatio: Float = 0.70f,
    val aspectRatio: PartialAspectRatio = PartialAspectRatio.FREE
)

enum class LaserStyle(val label: String) {
    DOT("Glowing Dot"),
    RING("Pulsing Ring"),
    TRAIL("Dynamic Light Trail"),
    SPOTLIGHT("Mini Spotlight")
}

data class LaserPointerConfig(
    val isEnabled: Boolean = false,
    val style: LaserStyle = LaserStyle.TRAIL,
    val sizeDp: Float = 28f,
    val color: Color = Color(0xFFFF2A6D),
    val opacity: Float = 0.9f,
    val trailLength: Int = 18
)

enum class SpotlightShape(val label: String) {
    CIRCLE("Circular Spotlight"),
    ROUNDED_RECT("Rounded Rectangle")
}

data class SpotlightConfig(
    val isEnabled: Boolean = false,
    val shape: SpotlightShape = SpotlightShape.CIRCLE,
    val sizeDp: Float = 160f,
    val darkness: Float = 0.75f,
    val featherDp: Float = 16f,
    val xRatio: Float = 0.5f,
    val yRatio: Float = 0.4f
)

enum class MagnifierShape(val label: String) {
    CIRCLE("Circle"),
    SQUARE("Square"),
    ROUNDED_RECT("Rounded Rect")
}

data class MagnifierConfig(
    val isEnabled: Boolean = false,
    val zoomLevel: Float = 2.0f,
    val shape: MagnifierShape = MagnifierShape.CIRCLE,
    val sizeDp: Float = 150f,
    val xRatio: Float = 0.5f,
    val yRatio: Float = 0.35f
)
