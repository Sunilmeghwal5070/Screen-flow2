package com.example.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropRotate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RecordingEntity
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
import com.example.util.DeviceCapabilities
import java.io.File

@Composable
fun VideoPlayerAndEditorDialog(
    recording: RecordingEntity,
    isEditorMode: Boolean = false,
    exportProgress: Float? = null,
    onClose: () -> Unit,
    onShare: () -> Unit,
    onExportEdited: (
        original: RecordingEntity,
        trimStartMs: Long,
        trimEndMs: Long,
        speedMultiplier: Float,
        rotateDegrees: Int,
        isMuted: Boolean,
        watermark: String?
    ) -> Unit
) {
    val accent = LocalAccentTheme.current

    val totalDurationMs = recording.durationMs.coerceAtLeast(1000L)
    var trimRange by remember { mutableStateOf(0f..totalDurationMs.toFloat()) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var rotateDegrees by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var customWatermark by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(StudioSurfaceLight)
                .testTag("video_player_editor_dialog"),
            color = StudioSurfaceLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEditorMode) "Quick Video Editor" else "Video Player",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary
                            )
                        )
                        Text(
                            text = recording.title,
                            style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary),
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StudioTextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Video View Container
                val videoFile = File(recording.filePath)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (videoFile.exists()) {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setVideoURI(Uri.fromFile(videoFile))
                                    val mediaController = MediaController(ctx)
                                    mediaController.setAnchorView(this)
                                    setMediaController(mediaController)
                                    start()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Video file preview", color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Editor Options / Player Controls
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Export progress banner
                    if (exportProgress != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = accent.container,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Exporting edited video...", fontWeight = FontWeight.Bold, color = accent.primary)
                                        Text("${(exportProgress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = accent.primary)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = exportProgress,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = accent.primary,
                                        trackColor = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Trim Slider
                    item {
                        SettingGroupCard(
                            title = "Trim Duration",
                            subtitle = "Start: ${DeviceCapabilities.formatDuration(trimRange.start.toLong())} · End: ${DeviceCapabilities.formatDuration(trimRange.endInclusive.toLong())}"
                        ) {
                            RangeSlider(
                                value = trimRange,
                                onValueChange = { trimRange = it },
                                valueRange = 0f..totalDurationMs.toFloat(),
                                colors = SliderDefaults.colors(
                                    thumbColor = accent.primary,
                                    activeTrackColor = accent.primary,
                                    inactiveTrackColor = StudioCardBorder
                                )
                            )
                        }
                    }

                    // Speed Selector
                    item {
                        SpecSelectorRow(
                            title = "Playback & Export Speed",
                            selectedOption = "${playbackSpeed}x",
                            options = listOf("0.5x", "1.0x", "1.5x", "2.0x"),
                            onOptionSelected = {
                                playbackSpeed = it.replace("x", "").toFloat()
                            }
                        )
                    }

                    // Audio Mute Toggle
                    item {
                        ToggleRow(
                            title = "Mute Audio Track",
                            description = "Remove all microphone and system audio from exported video",
                            checked = isMuted,
                            onCheckedChange = { isMuted = it }
                        )
                    }

                    // Watermark
                    item {
                        OutlinedTextField(
                            value = customWatermark,
                            onValueChange = { customWatermark = it },
                            label = { Text("Watermark text (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onExportEdited(
                                recording,
                                trimRange.start.toLong(),
                                trimRange.endInclusive.toLong(),
                                playbackSpeed,
                                rotateDegrees,
                                isMuted,
                                customWatermark.ifBlank { null }
                            )
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Video", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
