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
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BrandingWatermark
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AudioVUMeter
import com.example.ui.components.FacecamFloatingPreview
import com.example.ui.components.SettingGroupCard
import com.example.ui.components.SpecSelectorRow
import com.example.ui.components.StorageBarIndicator
import com.example.ui.components.TouchRippleCanvas
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.viewmodel.FacecamShape
import com.example.ui.viewmodel.TouchStyle
import com.example.util.DeviceCapabilities

@Composable
fun ToolsScreen(
    liveAudioLevel: Float,
    isFacecamEnabled: Boolean,
    facecamShape: FacecamShape,
    isFrontCamera: Boolean,
    touchStyle: TouchStyle,
    watermarkText: String,
    appStorageBytes: Long,
    screenshotStorageBytes: Long,
    isLaserActive: Boolean,
    isSpotlightActive: Boolean,
    isMagnifierActive: Boolean,
    isPrivacyActive: Boolean,
    onSetFacecamShape: (FacecamShape) -> Unit,
    onToggleCameraLens: () -> Unit,
    onSetTouchStyle: (TouchStyle) -> Unit,
    onSetWatermarkText: (String) -> Unit,
    onOpenDrawingWorkbench: () -> Unit,
    onOpenFloatingStudio: () -> Unit,
    onOpenPrivacyStudio: () -> Unit,
    onOpenPartialSelector: () -> Unit,
    onToggleLaser: () -> Unit,
    onToggleSpotlight: () -> Unit,
    onToggleMagnifier: () -> Unit,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current
    val freeBytes = DeviceCapabilities.getFreeStorageBytes(context)

    var cacheClearedMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("tools_screen_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Creator Tools Hub",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = StudioTextPrimary
                    )
                )
                Text(
                    text = "Professional studio utilities, floating smart panels, privacy shield, and screen markup.",
                    style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                )
            }
        }

        // 1. Floating Smart Panel Studio Card
        item {
            SettingGroupCard(
                title = "Floating Smart Panel Studio",
                subtitle = "Configure original floating recorder bubble, shapes, edge-snap, gestures & action matrices",
                icon = Icons.Default.Widgets
            ) {
                Button(
                    onClick = onOpenFloatingStudio,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch Floating Smart Panel Studio", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Privacy Shield Studio Card
        item {
            SettingGroupCard(
                title = "Privacy Shield Engine",
                subtitle = "Define Gaussian blur and mosaic zones over passwords, OTPs, and private info",
                icon = Icons.Default.Security
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenPrivacyStudio,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Configure Zones", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onOpenPrivacyStudio,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isPrivacyActive) "Shield: Active" else "Shield: Ready", fontSize = 12.sp)
                    }
                }
            }
        }

        // 3. Partial Screen Area Selector
        item {
            SettingGroupCard(
                title = "Partial Screen Capture",
                subtitle = "Crop video capture to specific app regions with 16:9, 9:16, 1:1 ratio presets",
                icon = Icons.Default.AspectRatio
            ) {
                Button(
                    onClick = onOpenPartialSelector,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(Icons.Default.AspectRatio, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch Partial Screen Selector", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. Tutorial Presenter Tools (Laser & Spotlight & Magnifier)
        item {
            SettingGroupCard(
                title = "Tutorial & Presentation Tools",
                subtitle = "Interactive tools for tech educators, app demos, and bug reports",
                icon = Icons.Default.Flare
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onToggleLaser,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLaserActive) Color(0xFFFF2A6D) else Color(0xFF334155)
                        )
                    ) {
                        Icon(Icons.Default.Flare, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isLaserActive) "Laser On" else "Laser", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onToggleSpotlight,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpotlightActive) Color(0xFFFBBF24) else Color(0xFF334155)
                        )
                    ) {
                        Icon(Icons.Default.Highlight, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isSpotlightActive) "Spotlight On" else "Spotlight", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onToggleMagnifier,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMagnifierActive) accent.primary else Color(0xFF334155)
                        )
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isMagnifierActive) "Loupe On" else "Loupe", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. Live Screen Drawing & Markup Workbench
        item {
            SettingGroupCard(
                title = "Screen Annotation & Whiteboard",
                subtitle = "Draw live arrows, highlight important areas, and markup screen captures",
                icon = Icons.Default.Draw
            ) {
                Button(
                    onClick = onOpenDrawingWorkbench,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                ) {
                    Icon(Icons.Default.Brush, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch Annotation Canvas", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 6. Interactive Touch & Pointer Visualizer
        item {
            SettingGroupCard(
                title = "Touch & Ripple Visualizer",
                subtitle = "Tap inside the box below to test touch indicator animations",
                icon = Icons.Default.TouchApp
            ) {
                SpecSelectorRow(
                    title = "Selected Style",
                    selectedOption = touchStyle.name,
                    options = listOf("RIPPLE", "DOT", "RING", "FINGER"),
                    onOptionSelected = { onSetTouchStyle(TouchStyle.valueOf(it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    TouchRippleCanvas(
                        touchStyle = touchStyle,
                        touchColor = accent.primary,
                        baseSizeDp = 42
                    )

                    Text(
                        text = "TAP HERE TO TEST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }

        // 7. Audio Decibel Level Studio
        item {
            SettingGroupCard(
                title = "Audio Level Calibration",
                subtitle = "Speak into microphone to verify live input sensitivity",
                icon = Icons.Default.GraphicEq
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = accent.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Microphone VU Meter",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = StudioTextPrimary)
                        )
                    }

                    AudioVUMeter(
                        amplitude = liveAudioLevel,
                        barCount = 12,
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }

        // 8. Facecam PiP Studio
        item {
            SettingGroupCard(
                title = "Facecam PiP Camera Viewfinder",
                subtitle = "Preview camera framing, aspect ratio, and background lens",
                icon = Icons.Default.Videocam
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FacecamFloatingPreview(
                        shape = facecamShape,
                        sizeDp = 84,
                        isFrontCamera = isFrontCamera,
                        hasCameraPermission = false,
                        onToggleCamera = onToggleCameraLens
                    )

                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        SpecSelectorRow(
                            title = "Shape",
                            selectedOption = facecamShape.name,
                            options = listOf("CIRCLE", "ROUNDED_RECT", "SQUARE"),
                            onOptionSelected = { onSetFacecamShape(FacecamShape.valueOf(it)) }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = onToggleCameraLens,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isFrontCamera) "Use Back Cam" else "Use Front Cam", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 9. Creator Brand Kit & Watermark
        item {
            SettingGroupCard(
                title = "Creator Brand Kit",
                subtitle = "Set your channel username, handle, or custom watermark signature",
                icon = Icons.Default.BrandingWatermark
            ) {
                OutlinedTextField(
                    value = watermarkText,
                    onValueChange = onSetWatermarkText,
                    label = { Text("Creator Watermark Text") },
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

        // 10. Storage Cleaner & Cache
        item {
            SettingGroupCard(
                title = "Storage Cleaner & Cache",
                subtitle = "${DeviceCapabilities.formatBytes(freeBytes)} free storage remaining",
                icon = Icons.Default.CleaningServices
            ) {
                StorageBarIndicator(
                    freeBytes = freeBytes,
                    appBytes = appStorageBytes,
                    screenshotBytes = screenshotStorageBytes
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        onClearCache()
                        cacheClearedMessage = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Icon(Icons.Default.CleaningServices, contentDescription = null, tint = StudioTextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (cacheClearedMessage) "Cache & Thumbnails Cleared!" else "Clear App Cache & Temp Files",
                        color = StudioTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
