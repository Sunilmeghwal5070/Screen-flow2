package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BrandingWatermark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CropRotate
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.RecordingStatus
import com.example.ui.components.AudioVUMeter
import com.example.ui.components.FacecamFloatingPreview
import com.example.ui.components.LiveOverlayPreviewBox
import com.example.ui.components.SettingGroupCard
import com.example.ui.components.SpecSelectorRow
import com.example.ui.components.ToggleRow
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.viewmodel.FacecamPosition
import com.example.ui.viewmodel.FacecamShape
import com.example.ui.viewmodel.TouchStyle
import com.example.util.DeviceCapabilities

@Composable
fun RecordScreen(
    recordingStatus: RecordingStatus,
    resolution: String,
    fps: Int,
    bitrateMbps: Int,
    audioSource: String,
    orientation: String,
    countdownSeconds: Int,
    autoStopMinutes: Int,
    isTouchEnabled: Boolean,
    touchStyle: TouchStyle,
    touchSizeDp: Int,
    isFacecamEnabled: Boolean,
    facecamShape: FacecamShape,
    facecamPosition: FacecamPosition,
    facecamSizeDp: Int,
    isFrontCamera: Boolean,
    isWatermarkEnabled: Boolean,
    watermarkText: String,
    liveAudioLevel: Float,
    onResolutionChange: (String, Int, Int, Int) -> Unit,
    onFpsChange: (Int) -> Unit,
    onBitrateChange: (Int) -> Unit,
    onAudioSourceChange: (String) -> Unit,
    onOrientationChange: (String) -> Unit,
    onCountdownChange: (Int) -> Unit,
    onAutoStopChange: (Int) -> Unit,
    onToggleTouch: (Boolean) -> Unit,
    onSetTouchStyle: (TouchStyle) -> Unit,
    onSetTouchSize: (Int) -> Unit,
    onToggleFacecam: (Boolean) -> Unit,
    onSetFacecamShape: (FacecamShape) -> Unit,
    onSetFacecamPosition: (FacecamPosition) -> Unit,
    onSetFacecamSize: (Int) -> Unit,
    onToggleCameraLens: () -> Unit,
    onToggleWatermark: (Boolean) -> Unit,
    onSetWatermarkText: (String) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onSaveCustomPreset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current
    val isRecording = recordingStatus is RecordingStatus.Recording

    val supportedResolutions = remember { DeviceCapabilities.getSupportedResolutions(context) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("record_studio_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
    ) {
        // Studio Title
        item {
            Column {
                Text(
                    text = "Studio Control Center",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = StudioTextPrimary
                    )
                )
                Text(
                    text = "Customize every pixel, audio channel, and overlay before recording.",
                    style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                )
            }
        }

        // Live Interactive Canvas Preview
        item {
            LiveOverlayPreviewBox(
                resolutionLabel = resolution,
                fps = fps,
                audioSource = audioSource,
                isFacecamEnabled = isFacecamEnabled,
                facecamShape = facecamShape,
                facecamPosition = facecamPosition,
                isTouchEnabled = isTouchEnabled,
                touchStyle = touchStyle,
                isWatermarkEnabled = isWatermarkEnabled,
                watermarkText = watermarkText,
                liveAudioLevel = liveAudioLevel
            )
        }

        // Big Primary CTA Button
        item {
            if (isRecording) {
                Button(
                    onClick = onStopRecording,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("record_screen_stop_cta"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRecordRed),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stop Recording",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
            } else {
                Button(
                    onClick = onStartRecording,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("record_screen_start_cta"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = null,
                        tint = StudioRecordRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Studio Recording",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
            }
        }

        // 1. Resolution & Video Encoding
        item {
            SettingGroupCard(
                title = "Video Quality & Encoding",
                subtitle = "Hardware accelerated H.264 MP4 export",
                icon = Icons.Default.HighQuality
            ) {
                // Resolution Selector
                Text(
                    text = "Resolution",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    supportedResolutions.take(4).forEach { resOpt ->
                        val isSelected = resolution == resOpt.label.substringBefore(" ")
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) accent.primary else accent.container.copy(alpha = 0.5f))
                                .border(1.dp, if (isSelected) accent.primary else StudioCardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    onResolutionChange(
                                        resOpt.label.substringBefore(" "),
                                        resOpt.width,
                                        resOpt.height,
                                        resOpt.recommendedBitrateMbps
                                    )
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = resOpt.label.substringBefore(" "),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else StudioTextPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FPS Selector
                SpecSelectorRow(
                    title = "Frame Rate (FPS)",
                    selectedOption = "${fps}fps",
                    options = listOf("24fps", "30fps", "60fps"),
                    onOptionSelected = { onFpsChange(it.replace("fps", "").toInt()) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bitrate Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Encoding Bitrate",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = StudioTextSecondary)
                    )
                    Text(
                        text = "$bitrateMbps Mbps",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = accent.primary)
                    )
                }

                Slider(
                    value = bitrateMbps.toFloat(),
                    onValueChange = { onBitrateChange(it.toInt()) },
                    valueRange = 4f..28f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = accent.primary,
                        activeTrackColor = accent.primary,
                        inactiveTrackColor = StudioCardBorder
                    )
                )

                // Orientation
                SpecSelectorRow(
                    title = "Capture Orientation",
                    selectedOption = orientation,
                    options = listOf("Portrait", "Landscape", "Auto"),
                    onOptionSelected = onOrientationChange
                )
            }
        }

        // 2. Audio Studio
        item {
            SettingGroupCard(
                title = "Audio Studio & Microphone",
                subtitle = "Select audio input channels and test microphone levels",
                icon = Icons.Default.GraphicEq
            ) {
                SpecSelectorRow(
                    title = "Audio Source",
                    selectedOption = audioSource,
                    options = listOf("Device + Mic", "Device Audio", "Microphone", "No Audio"),
                    onOptionSelected = onAudioSourceChange
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Live Audio VU Meter row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = accent.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live Mic Monitor",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = StudioTextPrimary)
                        )
                    }

                    AudioVUMeter(
                        amplitude = liveAudioLevel,
                        barCount = 14,
                        modifier = Modifier.height(20.dp)
                    )
                }
            }
        }

        // 3. Touch Visualization Studio
        item {
            SettingGroupCard(
                title = "Touch & Pointer Effects",
                subtitle = "Show touch points, animated ripples, and finger indicators",
                icon = Icons.Default.TouchApp
            ) {
                ToggleRow(
                    title = "Display Touches",
                    description = "Show interactive visual animations when tapping the screen",
                    checked = isTouchEnabled,
                    onCheckedChange = onToggleTouch
                )

                if (isTouchEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SpecSelectorRow(
                        title = "Animation Style",
                        selectedOption = touchStyle.name,
                        options = listOf("RIPPLE", "DOT", "RING", "FINGER"),
                        onOptionSelected = { onSetTouchStyle(TouchStyle.valueOf(it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Indicator Size",
                            style = MaterialTheme.typography.labelMedium.copy(color = StudioTextSecondary)
                        )
                        Text(
                            text = "${touchSizeDp}dp",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = accent.primary)
                        )
                    }

                    Slider(
                        value = touchSizeDp.toFloat(),
                        onValueChange = { onSetTouchSize(it.toInt()) },
                        valueRange = 24f..72f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = accent.primary,
                            activeTrackColor = accent.primary,
                            inactiveTrackColor = StudioCardBorder
                        )
                    )
                }
            }
        }

        // 4. Facecam Studio
        item {
            SettingGroupCard(
                title = "Facecam PiP Overlay",
                subtitle = "Floating camera picture-in-picture with customizable shape",
                icon = Icons.Default.Videocam
            ) {
                ToggleRow(
                    title = "Enable Facecam",
                    description = "Add a floating selfie camera over your recording",
                    checked = isFacecamEnabled,
                    onCheckedChange = onToggleFacecam
                )

                if (isFacecamEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))

                    SpecSelectorRow(
                        title = "Facecam Shape",
                        selectedOption = facecamShape.name,
                        options = listOf("CIRCLE", "ROUNDED_RECT", "SQUARE"),
                        onOptionSelected = { onSetFacecamShape(FacecamShape.valueOf(it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SpecSelectorRow(
                        title = "Default Corner Position",
                        selectedOption = when (facecamPosition) {
                            FacecamPosition.TOP_LEFT -> "Top L"
                            FacecamPosition.TOP_RIGHT -> "Top R"
                            FacecamPosition.BOTTOM_LEFT -> "Bot L"
                            FacecamPosition.BOTTOM_RIGHT -> "Bot R"
                        },
                        options = listOf("Top L", "Top R", "Bot L", "Bot R"),
                        onOptionSelected = {
                            val pos = when (it) {
                                "Top L" -> FacecamPosition.TOP_LEFT
                                "Top R" -> FacecamPosition.TOP_RIGHT
                                "Bot L" -> FacecamPosition.BOTTOM_LEFT
                                else -> FacecamPosition.BOTTOM_RIGHT
                            }
                            onSetFacecamPosition(pos)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onToggleCameraLens,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, accent.primary)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = accent.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFrontCamera) "Switch to Back Camera" else "Switch to Front Selfie Camera",
                            color = accent.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 5. Watermark & Creator Branding
        item {
            SettingGroupCard(
                title = "Watermark & Brand Kit",
                subtitle = "Add your creator username or channel watermark",
                icon = Icons.Default.BrandingWatermark
            ) {
                ToggleRow(
                    title = "Include Watermark",
                    description = "Burn custom text watermark in the corner of recordings",
                    checked = isWatermarkEnabled,
                    onCheckedChange = onToggleWatermark
                )

                if (isWatermarkEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = watermarkText,
                        onValueChange = onSetWatermarkText,
                        label = { Text("Watermark Text / Username") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent.primary,
                            unfocusedBorderColor = StudioCardBorder
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // 6. Automation & Timers
        item {
            SettingGroupCard(
                title = "Automation & Countdown",
                subtitle = "Countdown timer and auto-stop recording limits",
                icon = Icons.Default.Timer
            ) {
                SpecSelectorRow(
                    title = "Start Countdown",
                    selectedOption = if (countdownSeconds == 0) "None" else "${countdownSeconds}s",
                    options = listOf("None", "3s", "5s", "10s"),
                    onOptionSelected = {
                        val sec = when (it) {
                            "None" -> 0
                            "3s" -> 3
                            "5s" -> 5
                            "10s" -> 10
                            else -> 3
                        }
                        onCountdownChange(sec)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SpecSelectorRow(
                    title = "Auto-Stop Duration",
                    selectedOption = if (autoStopMinutes == 0) "Disabled" else "${autoStopMinutes}m",
                    options = listOf("Disabled", "5m", "15m", "30m", "60m"),
                    onOptionSelected = {
                        val mins = when (it) {
                            "Disabled" -> 0
                            "5m" -> 5
                            "15m" -> 15
                            "30m" -> 30
                            "60m" -> 60
                            else -> 0
                        }
                        onAutoStopChange(mins)
                    }
                )
            }
        }

        // Save as Preset Action
        item {
            OutlinedButton(
                onClick = onSaveCustomPreset,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_preset_button"),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, accent.primary)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = accent.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Setup as New Preset",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = accent.primary)
                )
            }
        }
    }
}
