package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.viewmodel.FacecamPosition
import com.example.ui.viewmodel.FacecamShape
import com.example.ui.viewmodel.TouchStyle

@Composable
fun LiveOverlayPreviewBox(
    resolutionLabel: String = "1080p",
    fps: Int = 60,
    audioSource: String = "Device + Mic",
    isFacecamEnabled: Boolean = false,
    facecamShape: FacecamShape = FacecamShape.CIRCLE,
    facecamPosition: FacecamPosition = FacecamPosition.TOP_RIGHT,
    isTouchEnabled: Boolean = true,
    touchStyle: TouchStyle = TouchStyle.RIPPLE,
    isWatermarkEnabled: Boolean = false,
    watermarkText: String = "@screenflow",
    liveAudioLevel: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_overlay_preview"),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, StudioCardBorder),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = accent.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Studio Canvas Preview",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.container)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$resolutionLabel · ${fps}fps",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = accent.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Simulated Device Screen Mockup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        )
                    )
                    .border(2.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            ) {
                // Interactive Touch Canvas Layer inside phone
                if (isTouchEnabled) {
                    TouchRippleCanvas(
                        touchStyle = touchStyle,
                        touchColor = accent.primary,
                        baseSizeDp = 36
                    )
                }

                // Center Mock Wallpaper / App Content Preview
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "TAP HERE TO TEST TOUCH RIPPLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Top Minimal Recording Pill Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StudioRecordRed)
                        )
                        Text(
                            text = "00:14",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        // Audio meter
                        AudioVUMeter(
                            amplitude = liveAudioLevel,
                            barCount = 8,
                            modifier = Modifier.height(14.dp)
                        )
                    }
                }

                // Facecam PiP Preview
                if (isFacecamEnabled) {
                    val facecamAlign = when (facecamPosition) {
                        FacecamPosition.TOP_LEFT -> Alignment.TopStart
                        FacecamPosition.TOP_RIGHT -> Alignment.TopEnd
                        FacecamPosition.BOTTOM_LEFT -> Alignment.BottomStart
                        FacecamPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                    }

                    Box(
                        modifier = Modifier
                            .align(facecamAlign)
                            .padding(10.dp)
                    ) {
                        FacecamFloatingPreview(
                            shape = facecamShape,
                            sizeDp = 64,
                            isFrontCamera = true,
                            hasCameraPermission = false
                        )
                    }
                }

                // Watermark overlay
                if (isWatermarkEnabled && watermarkText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = watermarkText,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Quick Recording Mini Action Controls
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = StudioRecordRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Specs badges footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpecBadge(
                    icon = Icons.Default.Mic,
                    title = "Audio",
                    value = audioSource
                )
                SpecBadge(
                    icon = Icons.Default.TouchApp,
                    title = "Touch",
                    value = if (isTouchEnabled) touchStyle.name else "OFF"
                )
                SpecBadge(
                    icon = Icons.Default.Videocam,
                    title = "Facecam",
                    value = if (isFacecamEnabled) facecamShape.name else "OFF"
                )
            }
        }
    }
}

@Composable
private fun SpecBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    val accent = LocalAccentTheme.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent.primary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "$title: ",
            style = MaterialTheme.typography.labelSmall.copy(color = StudioTextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        )
    }
}
