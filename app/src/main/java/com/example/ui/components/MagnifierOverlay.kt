package com.example.ui.components

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MagnifierConfig
import com.example.data.model.MagnifierShape
import com.example.ui.theme.LocalAccentTheme
import kotlin.math.roundToInt

@Composable
fun MagnifierOverlay(
    config: MagnifierConfig,
    onZoomChange: (Float) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current
    val density = LocalDensity.current.density

    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("magnifier_overlay")) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        var posX by remember { mutableFloatStateOf(widthPx * config.xRatio) }
        var posY by remember { mutableFloatStateOf(heightPx * config.yRatio) }
        var zoom by remember { mutableFloatStateOf(config.zoomLevel) }

        val sizePx = config.sizeDp * density

        val shape: Shape = when (config.shape) {
            MagnifierShape.CIRCLE -> CircleShape
            MagnifierShape.SQUARE -> RoundedCornerShape(4.dp)
            MagnifierShape.ROUNDED_RECT -> RoundedCornerShape(20.dp)
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
                .size(config.sizeDp.dp)
                .clip(shape)
                .border(3.dp, accent.primary, shape)
                .background(Color(0xFF0F172A).copy(alpha = 0.4f))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        posX = (posX + dragAmount.x).coerceIn(0f, widthPx - sizePx)
                        posY = (posY + dragAmount.y).coerceIn(0f, heightPx - sizePx)
                    }
                }
        ) {
            // Loupe Crosshairs & Grid Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f

                // Crosshair lines
                drawLine(
                    color = accent.primary.copy(alpha = 0.6f),
                    start = Offset(cx - 20f, cy),
                    end = Offset(cx + 20f, cy),
                    strokeWidth = 2f
                )
                drawLine(
                    color = accent.primary.copy(alpha = 0.6f),
                    start = Offset(cx, cy - 20f),
                    end = Offset(cx, cy + 20f),
                    strokeWidth = 2f
                )
                drawCircle(
                    color = accent.primary.copy(alpha = 0.5f),
                    radius = 12f,
                    center = Offset(cx, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                )
            }

            // Top Header: Zoom Pill & Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accent.primary
                ) {
                    Text(
                        text = "%.1fx".format(zoom),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Magnifier", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            // Bottom Zoom Selector (1.5x, 2x, 3x, 4x)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(1.5f, 2.0f, 3.0f, 4.0f).forEach { z ->
                    Text(
                        text = "${z}x",
                        color = if (zoom == z) accent.primary else Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = if (zoom == z) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}
