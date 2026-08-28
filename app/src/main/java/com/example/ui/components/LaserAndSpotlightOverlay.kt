package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LaserPointerConfig
import com.example.data.model.LaserStyle
import com.example.data.model.SpotlightConfig
import com.example.data.model.SpotlightShape
import kotlin.math.roundToInt

@Composable
fun LaserAndSpotlightOverlay(
    laserConfig: LaserPointerConfig,
    spotlightConfig: SpotlightConfig,
    onCloseLaser: () -> Unit,
    onCloseSpotlight: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("laser_spotlight_overlay")) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // 1. SCREEN SPOTLIGHT
        if (spotlightConfig.isEnabled) {
            var spotCenterX by remember { mutableFloatStateOf(widthPx * spotlightConfig.xRatio) }
            var spotCenterY by remember { mutableFloatStateOf(heightPx * spotlightConfig.yRatio) }
            val spotRadiusPx = spotlightConfig.sizeDp * density

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            spotCenterX = (spotCenterX + dragAmount.x).coerceIn(spotRadiusPx, widthPx - spotRadiusPx)
                            spotCenterY = (spotCenterY + dragAmount.y).coerceIn(spotRadiusPx, heightPx - spotRadiusPx)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Dark background with cutout
                    val darkColor = Color.Black.copy(alpha = spotlightConfig.darkness)

                    val path = Path().apply {
                        addRect(Rect(0f, 0f, size.width, size.height))
                        if (spotlightConfig.shape == SpotlightShape.CIRCLE) {
                            addOval(
                                Rect(
                                    center = Offset(spotCenterX, spotCenterY),
                                    radius = spotRadiusPx
                                )
                            )
                        } else {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(
                                        left = spotCenterX - spotRadiusPx * 1.2f,
                                        top = spotCenterY - spotRadiusPx * 0.7f,
                                        right = spotCenterX + spotRadiusPx * 1.2f,
                                        bottom = spotCenterY + spotRadiusPx * 0.7f
                                    ),
                                    cornerRadius = CornerRadius(24f, 24f)
                                )
                            )
                        }
                        fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                    }

                    drawPath(path, color = darkColor, style = Fill)

                    // Glowing ring around aperture
                    if (spotlightConfig.shape == SpotlightShape.CIRCLE) {
                        drawCircle(
                            color = Color(0xFFFBBF24).copy(alpha = 0.8f),
                            radius = spotRadiusPx,
                            center = Offset(spotCenterX, spotCenterY),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    } else {
                        drawRoundRect(
                            color = Color(0xFFFBBF24).copy(alpha = 0.8f),
                            topLeft = Offset(spotCenterX - spotRadiusPx * 1.2f, spotCenterY - spotRadiusPx * 0.7f),
                            size = Size(spotRadiusPx * 2.4f, spotRadiusPx * 1.4f),
                            cornerRadius = CornerRadius(24f, 24f),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }

                // Spotlight Header Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0F172A).copy(alpha = 0.9f),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Highlight, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Spotlight Mode (Drag to focus)",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = onCloseSpotlight, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close Spotlight", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // 2. LASER POINTER
        if (laserConfig.isEnabled) {
            val laserPoints = remember { mutableStateListOf<Offset>() }
            var currentPos by remember { mutableStateOf(Offset(widthPx * 0.5f, heightPx * 0.5f)) }

            val infiniteTransition = rememberInfiniteTransition(label = "laser_glow")
            val glowRadius by infiniteTransition.animateFloat(
                initialValue = 16f,
                targetValue = 28f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow_rad"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPos = offset
                                laserPoints.clear()
                                laserPoints.add(offset)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                currentPos = change.position
                                laserPoints.add(change.position)
                                if (laserPoints.size > laserConfig.trailLength) {
                                    laserPoints.removeAt(0)
                                }
                            },
                            onDragEnd = {
                                laserPoints.clear()
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw Trail if applicable
                    if (laserConfig.style == LaserStyle.TRAIL && laserPoints.size > 1) {
                        for (i in 0 until laserPoints.size - 1) {
                            val alpha = ((i + 1).toFloat() / laserPoints.size) * laserConfig.opacity
                            drawLine(
                                color = laserConfig.color.copy(alpha = alpha),
                                start = laserPoints[i],
                                end = laserPoints[i + 1],
                                strokeWidth = (laserConfig.sizeDp * 0.5f) * ((i + 1).toFloat() / laserPoints.size),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Outer Glowing Halo
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                laserConfig.color.copy(alpha = 0.85f * laserConfig.opacity),
                                laserConfig.color.copy(alpha = 0.2f * laserConfig.opacity),
                                Color.Transparent
                            ),
                            center = currentPos,
                            radius = glowRadius * (laserConfig.sizeDp / 24f)
                        ),
                        radius = glowRadius * (laserConfig.sizeDp / 24f),
                        center = currentPos
                    )

                    // Sharp Laser Core Dot
                    drawCircle(
                        color = Color.White,
                        radius = (laserConfig.sizeDp * 0.25f),
                        center = currentPos
                    )

                    // Outer ring for RING style
                    if (laserConfig.style == LaserStyle.RING) {
                        drawCircle(
                            color = laserConfig.color,
                            radius = (laserConfig.sizeDp * 0.7f),
                            center = currentPos,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                // Laser Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0F172A).copy(alpha = 0.9f),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Flare, contentDescription = null, tint = laserConfig.color, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Laser (${laserConfig.style.label.substringBefore(" ")})",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = onCloseLaser, modifier = Modifier.size(18.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close Laser", tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}
