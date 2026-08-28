package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SettingGroupCard
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun SettingsScreen(
    currentAccent: AccentTheme,
    resolution: String = "1080p",
    fps: Int = 60,
    audioSource: String = "Microphone",
    countdownSeconds: Int = 3,
    isFacecamEnabled: Boolean = false,
    onSelectAccent: (AccentTheme) -> Unit,
    onResolutionChange: ((String, Int, Int, Int) -> Unit)? = null,
    onFpsChange: ((Int) -> Unit)? = null,
    onAudioSourceChange: ((String) -> Unit)? = null,
    onCountdownChange: ((Int) -> Unit)? = null,
    onToggleFacecam: ((Boolean) -> Unit)? = null,
    onShowPermissions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .testTag("settings_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = StudioTextPrimary
                    )
                )
                Text(
                    text = "Customize video quality, audio, and app appearance.",
                    style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                )
            }
        }

        // 1. Video Quality Group
        item {
            SettingGroupCard(
                title = "Video Quality",
                subtitle = "Resolution and frame rate options",
                icon = Icons.Default.HighQuality
            ) {
                // Resolution Selector
                Text(
                    text = "Resolution",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                val resolutions = listOf(
                    Triple("1080p", 1920 to 1080, 16),
                    Triple("720p", 1280 to 720, 8),
                    Triple("480p", 854 to 480, 4)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    resolutions.forEach { (res, dims, br) ->
                        val isSelected = resolution.startsWith(res)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onResolutionChange?.invoke(res, dims.first, dims.second, br)
                                }
                                .testTag("res_option_$res"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) accent.container else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accent.primary else StudioCardBorder
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = res,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) accent.primary else StudioTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FPS Selector
                Text(
                    text = "Frame Rate (FPS)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                val fpsList = listOf(60, 30)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fpsList.forEach { f ->
                        val isSelected = fps == f
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onFpsChange?.invoke(f) }
                                .testTag("fps_option_$f"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) accent.container else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accent.primary else StudioCardBorder
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${f} FPS",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) accent.primary else StudioTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Audio & Timer Group
        item {
            SettingGroupCard(
                title = "Audio & Timer",
                subtitle = "Sound recording source and start countdown",
                icon = Icons.Default.Mic
            ) {
                // Audio Source
                Text(
                    text = "Audio Source",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                val audioSources = listOf("Microphone", "Device Audio", "Mute")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    audioSources.forEach { src ->
                        val isSelected = audioSource.equals(src, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onAudioSourceChange?.invoke(src) }
                                .testTag("audio_option_${src.lowercase()}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) accent.container else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accent.primary else StudioCardBorder
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = src,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) accent.primary else StudioTextPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Countdown Timer
                Text(
                    text = "Start Countdown",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                val countdowns = listOf(0 to "Off", 3 to "3s", 5 to "5s")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    countdowns.forEach { (sec, label) ->
                        val isSelected = countdownSeconds == sec
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onCountdownChange?.invoke(sec) }
                                .testTag("countdown_option_$sec"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) accent.container else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accent.primary else StudioCardBorder
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) accent.primary else StudioTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Floating Bubble & Controls
        item {
            SettingGroupCard(
                title = "Floating Control Bubble",
                subtitle = "Draggable quick menu over other apps & games",
                icon = Icons.Default.Layers
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "When you minimize the app while recording, a floating draggable bubble appears on your screen with quick controls (Pause, Resume, Stop, Screenshot, Return to App).",
                        style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                    )
                    OutlinedButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    val fallback = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(fallback)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("manage_overlay_permission_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = accent.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Display Over Other Apps Permission",
                            color = accent.primary,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }

        // 4. Floating Facecam
        item {
            SettingGroupCard(
                title = "Floating Facecam",
                subtitle = "Show your camera preview on screen",
                icon = Icons.Default.CameraAlt
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Facecam",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary
                            )
                        )
                        Text(
                            text = "Show selfie camera bubble while recording",
                            style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                        )
                    }

                    Switch(
                        checked = isFacecamEnabled,
                        onCheckedChange = { onToggleFacecam?.invoke(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accent.primary
                        )
                    )
                }
            }
        }

        // 4. Accent Theme Palette
        item {
            SettingGroupCard(
                title = "App Accent Theme",
                subtitle = "Choose your favorite interface color",
                icon = Icons.Default.Palette
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(AccentTheme.values()) { theme ->
                        val isSelected = theme == currentAccent
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectAccent(theme) }
                                .padding(4.dp)
                                .testTag("theme_accent_${theme.id.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(theme.primary)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF0F172A) else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = theme.title.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (isSelected) StudioTextPrimary else StudioTextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. 100% Privacy Guarantee
        item {
            SettingGroupCard(
                title = "100% Private & Offline",
                subtitle = "Zero cloud uploads · Stored locally on your device",
                icon = Icons.Default.Shield
            ) {
                Text(
                    text = "All recordings, audio, and screenshots are processed strictly on-device. No data ever leaves your phone.",
                    style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                )
            }
        }
    }
}
