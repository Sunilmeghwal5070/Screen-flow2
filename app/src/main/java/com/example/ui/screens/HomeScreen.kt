package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropRotate
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PresetEntity
import com.example.data.model.RecordingEntity
import com.example.service.RecordingStatus
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.viewmodel.NavigationTab
import com.example.util.DeviceCapabilities
import com.example.util.VideoMetadataHelper
import java.io.File

@Composable
fun HomeScreen(
    recordingStatus: RecordingStatus,
    resolution: String,
    fps: Int,
    audioSource: String,
    orientation: String,
    bitrateMbps: Int,
    liveAudioLevel: Float,
    presets: List<PresetEntity>,
    recentRecordings: List<RecordingEntity>,
    appStorageBytes: Long,
    screenshotStorageBytes: Long,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onQuickRecord: () -> Unit,
    onTakeScreenshot: () -> Unit,
    onSelectPreset: (PresetEntity) -> Unit,
    onOpenWizard: () -> Unit,
    onOpenDrawingWorkbench: () -> Unit,
    onOpenStorageManager: () -> Unit,
    onNavigateTab: (NavigationTab) -> Unit,
    onPlayRecording: (RecordingEntity) -> Unit,
    onEditRecording: (RecordingEntity) -> Unit,
    onShareRecording: (RecordingEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current
    val freeBytes = DeviceCapabilities.getFreeStorageBytes(context)
    val remainingMins = DeviceCapabilities.estimateRemainingRecordingMinutes(freeBytes, bitrateMbps)
    val isRecording = recordingStatus is RecordingStatus.Recording

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .testTag("home_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // 1. Storage & Quality Quick Status
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = StudioSurfaceLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isRecording) StudioRecordRed else accent.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRecording) "Recording Active" else "Ready to Record",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary
                            )
                        )
                    }

                    Text(
                        text = "${DeviceCapabilities.formatBytes(freeBytes)} free · ~${remainingMins}m",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = StudioTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // 2. Main Hero Record Card
        item {
            HeroRecordCard(
                isRecording = isRecording,
                recordingStatus = recordingStatus,
                resolution = resolution,
                fps = fps,
                audioSource = audioSource,
                orientation = orientation,
                onStartRecording = onStartRecording,
                onStopRecording = onStopRecording,
                onNavigateSettings = { onNavigateTab(NavigationTab.SETTINGS) }
            )
        }

        // 3. Quick Action Chips (Screenshot & Settings)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionPill(
                    title = "Take Screenshot",
                    icon = Icons.Default.CameraAlt,
                    accentColor = Color(0xFF2563EB),
                    backgroundColor = Color(0xFFEFF6FF),
                    onClick = onTakeScreenshot,
                    modifier = Modifier.weight(1f)
                )

                QuickActionPill(
                    title = "Settings",
                    icon = Icons.Default.Speed,
                    accentColor = accent.primary,
                    backgroundColor = accent.container,
                    onClick = { onNavigateTab(NavigationTab.SETTINGS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Quick Presets Bar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recording Presets",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(presets) { preset ->
                        val isSelected = preset.resolution == resolution && preset.fps == fps
                        SimplePresetCard(
                            preset = preset,
                            isSelected = isSelected,
                            onSelect = { onSelectPreset(preset) }
                        )
                    }
                }
            }
        }

        // 5. Recent Recordings
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Recordings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )
                    )
                    if (recentRecordings.isNotEmpty()) {
                        Text(
                            text = "View all",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = accent.primary
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigateTab(NavigationTab.LIBRARY) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (recentRecordings.isEmpty()) {
                    SimpleEmptyState(onStart = onStartRecording)
                } else {
                    recentRecordings.take(3).forEach { recording ->
                        SimpleRecordingCard(
                            recording = recording,
                            onPlay = { onPlayRecording(recording) },
                            onShare = { onShareRecording(recording) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroRecordCard(
    isRecording: Boolean,
    recordingStatus: RecordingStatus,
    resolution: String,
    fps: Int,
    audioSource: String,
    orientation: String,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    val accent = LocalAccentTheme.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_recording_card"),
        shape = RoundedCornerShape(24.dp),
        color = StudioSurfaceLight,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, StudioCardBorder),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Status Text
            if (isRecording) {
                val durationSec = ((recordingStatus as? RecordingStatus.Recording)?.durationMs ?: 0L) / 1000L
                val mins = durationSec / 60
                val secs = durationSec % 60
                val timeFormatted = String.format("%02d:%02d", mins, secs)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(StudioRecordRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RECORDING",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = StudioRecordRed,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                )
            } else {
                Text(
                    text = "Screen Recorder",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "High quality capture with clear audio",
                    style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Big Action Button
            if (isRecording) {
                Button(
                    onClick = onStopRecording,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("stop_recording_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRecordRed),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Recording",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stop & Save Video",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            } else {
                // Large circular Start Recording Button
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    StudioRecordRed.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(StudioRecordRed, Color(0xFFDC2626))
                            )
                        )
                        .clickable { onStartRecording() }
                        .testTag("start_recording_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Start Recording",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tap to Start Recording",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Current Quality Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F5F9))
                    .clickable { onNavigateSettings() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HighQuality,
                        contentDescription = null,
                        tint = accent.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "$resolution · ${fps}fps",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )
                    )
                }

                Text(
                    text = audioSource,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = StudioTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun QuickActionPill(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("quick_pill_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                )
            )
        }
    }
}

@Composable
private fun SimplePresetCard(
    preset: PresetEntity,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val accent = LocalAccentTheme.current

    Surface(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .testTag("preset_card_${preset.name.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) accent.container else StudioSurfaceLight,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) accent.primary else StudioCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${preset.resolution} · ${preset.fps}fps",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSelected) accent.primary else StudioTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(accent.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = preset.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SimpleRecordingCard(
    recording: RecordingEntity,
    onPlay: () -> Unit,
    onShare: () -> Unit
) {
    val accent = LocalAccentTheme.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .testTag("recent_recording_card"),
        shape = RoundedCornerShape(16.dp),
        color = StudioSurfaceLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail Box
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                if (recording.thumbnailPath != null && File(recording.thumbnailPath).exists()) {
                    AsyncImage(
                        model = recording.thumbnailPath,
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (recording.durationMs > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.8f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = DeviceCapabilities.formatDuration(recording.durationMs),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Video Meta
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recording.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${recording.resolution} · ${DeviceCapabilities.formatBytes(recording.fileSizeBytes)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = StudioTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }

            // Share Action
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SimpleEmptyState(onStart: () -> Unit) {
    val accent = LocalAccentTheme.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No recordings yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap the big red button above to start your first recording.",
                style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary),
                textAlign = TextAlign.Center
            )
        }
    }
}
