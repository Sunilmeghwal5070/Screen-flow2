package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

data class WizardOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val resolution: String,
    val width: Int,
    val height: Int,
    val fps: Int,
    val bitrate: Int,
    val audio: String,
    val touch: Boolean,
    val facecam: Boolean
)

@Composable
fun SmartWizardDialog(
    onApplySettings: (
        resolution: String,
        width: Int,
        height: Int,
        fps: Int,
        bitrate: Int,
        audio: String,
        touch: Boolean,
        facecam: Boolean
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val accent = LocalAccentTheme.current

    val wizardChoices = listOf(
        WizardOption(
            title = "Gaming Session",
            description = "1080p FHD · 60 FPS · 16 Mbps · Device + Mic Audio · Facecam PiP",
            icon = Icons.Default.SportsEsports,
            resolution = "1080p",
            width = 1080,
            height = 1920,
            fps = 60,
            bitrate = 16,
            audio = "Device + Mic",
            touch = false,
            facecam = true
        ),
        WizardOption(
            title = "App Tutorial & Walkthrough",
            description = "1080p FHD · 30 FPS · 12 Mbps · Mic Only · Touch Ripples Active",
            icon = Icons.Default.SmartDisplay,
            resolution = "1080p",
            width = 1080,
            height = 1920,
            fps = 30,
            bitrate = 12,
            audio = "Microphone",
            touch = true,
            facecam = true
        ),
        WizardOption(
            title = "Bug Report / Quick Issue",
            description = "720p HD · 30 FPS · 8 Mbps · Touch Indicators · Small file size",
            icon = Icons.Default.BugReport,
            resolution = "720p",
            width = 720,
            height = 1280,
            fps = 30,
            bitrate = 8,
            audio = "Microphone",
            touch = true,
            facecam = false
        ),
        WizardOption(
            title = "Master 1440p 2K Studio",
            description = "1440p 2K · 60 FPS · 24 Mbps · Uncompressed Stereo Audio",
            icon = Icons.Default.AutoAwesome,
            resolution = "1440p",
            width = 1440,
            height = 2560,
            fps = 60,
            bitrate = 24,
            audio = "Device + Mic",
            touch = true,
            facecam = false
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("smart_wizard_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = StudioSurfaceLight,
            shadowElevation = 16.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = accent.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Setup Wizard",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StudioTextSecondary)
                    }
                }

                Text(
                    text = "Select what you are creating, and ScreenFlow will configure the optimal resolution, bitrate, frame rate, and overlays automatically.",
                    style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                wizardChoices.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onApplySettings(
                                    option.resolution,
                                    option.width,
                                    option.height,
                                    option.fps,
                                    option.bitrate,
                                    option.audio,
                                    option.touch,
                                    option.facecam
                                )
                                onDismiss()
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(accent.container),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(option.icon, contentDescription = null, tint = accent.primary, modifier = Modifier.size(22.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = StudioTextPrimary
                                    )
                                )
                                Text(
                                    text = option.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = StudioTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePresetDialog(
    onSave: (name: String, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    val accent = LocalAccentTheme.current
    var presetName by remember { mutableStateOf("") }
    var presetDesc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Recording Preset", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Save your current studio configuration (resolution, frame rate, audio, facecam, touches) as a reusable 1-tap preset.",
                    style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                )

                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    label = { Text("Preset Name (e.g. My Twitch Stream)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent.primary)
                )

                OutlinedTextField(
                    value = presetDesc,
                    onValueChange = { presetDesc = it },
                    label = { Text("Short Description") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent.primary)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (presetName.isNotBlank()) {
                        onSave(presetName.trim(), presetDesc.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
            ) {
                Text("Save Preset", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PermissionsExplainerDialog(
    onDismiss: () -> Unit
) {
    val accent = LocalAccentTheme.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("permissions_explainer_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = StudioSurfaceLight,
            shadowElevation = 16.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Why ScreenFlow needs permissions",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                PermItem(
                    icon = Icons.Default.PhoneAndroid,
                    title = "Screen Capture (Media Projection)",
                    description = "Required to capture on-screen video frames to record your gameplay, apps, and tutorials."
                )

                PermItem(
                    icon = Icons.Default.Mic,
                    title = "Microphone Audio",
                    description = "Captures your voice narration alongside gameplay or system sounds."
                )

                PermItem(
                    icon = Icons.Default.Videocam,
                    title = "Camera (Facecam PiP)",
                    description = "Used solely to display your selfie camera in a picture-in-picture overlay."
                )

                PermItem(
                    icon = Icons.Default.Notifications,
                    title = "Foreground Notification",
                    description = "Maintains stable recording in the background without being closed by Android battery saver."
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                ) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PermItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    val accent = LocalAccentTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accent.container),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent.primary, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = StudioTextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun LowStorageWarningDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = StudioRecordRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Low Storage Warning", fontWeight = FontWeight.Bold, color = StudioRecordRed)
            }
        },
        text = {
            Text("Your device has less than 50MB of free space available. Please free up storage before recording to prevent corrupted video files.")
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = StudioRecordRed)
            ) {
                Text("Understood")
            }
        }
    )
}
