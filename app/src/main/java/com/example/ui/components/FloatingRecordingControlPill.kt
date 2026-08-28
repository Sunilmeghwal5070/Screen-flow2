package com.example.ui.components

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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioRecordRed
import com.example.util.DeviceCapabilities
import kotlin.math.roundToInt

@Composable
fun FloatingRecordingControlPill(
    durationMs: Long,
    isPaused: Boolean,
    onPauseResume: () -> Unit,
    onStop: () -> Unit,
    onScreenshot: () -> Unit,
    onToggleDraw: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current
    var isExpanded by remember { mutableStateOf(false) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "rec_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .testTag("floating_recording_pill")
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.92f),
            shadowElevation = 16.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pulse Dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .scale(if (isPaused) 1f else pulseScale)
                        .clip(CircleShape)
                        .background(if (isPaused) Color(0xFFF59E0B) else StudioRecordRed)
                )

                // Live Timer
                Text(
                    text = DeviceCapabilities.formatDuration(durationMs),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                )

                if (isPaused) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PAUSED",
                            color = Color(0xFFFBBF24),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                // Expanded Controls
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Pause / Resume
                        PillActionBtn(
                            icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            tint = Color.White,
                            bg = Color.White.copy(alpha = 0.15f),
                            onClick = onPauseResume
                        )

                        // Screenshot
                        PillActionBtn(
                            icon = Icons.Default.CameraAlt,
                            tint = Color.White,
                            bg = Color.White.copy(alpha = 0.15f),
                            onClick = onScreenshot
                        )

                        // Draw Toolbar Toggle
                        PillActionBtn(
                            icon = Icons.Default.Draw,
                            tint = accent.secondary,
                            bg = accent.primary.copy(alpha = 0.25f),
                            onClick = onToggleDraw
                        )

                        // Stop Recording
                        PillActionBtn(
                            icon = Icons.Default.Stop,
                            tint = Color.White,
                            bg = StudioRecordRed,
                            onClick = onStop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PillActionBtn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    bg: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}
