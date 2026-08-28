package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.TouchStyle

data class TouchPoint(val offset: Offset, val timestamp: Long = System.currentTimeMillis())

@Composable
fun TouchAndGestureVisualizer(
    touchStyle: TouchStyle,
    touchColor: Color,
    touchSizeDp: Int,
    showGestures: Boolean = true,
    modifier: Modifier = Modifier
) {
    val touches = remember { mutableStateListOf<TouchPoint>() }
    val gestureTrail = remember { mutableStateListOf<Offset>() }

    val infiniteTransition = rememberInfiniteTransition(label = "touch_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_radius"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    touches.add(TouchPoint(offset))
                    if (touches.size > 8) touches.removeAt(0)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        gestureTrail.clear()
                        gestureTrail.add(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        gestureTrail.add(change.position)
                        if (gestureTrail.size > 14) gestureTrail.removeAt(0)
                    },
                    onDragEnd = {
                        gestureTrail.clear()
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val now = System.currentTimeMillis()
            touches.removeAll { now - it.timestamp > 700 }

            val radiusPx = (touchSizeDp / 2).dp.toPx()

            // Draw Tap / Touch Effects
            touches.forEach { touch ->
                val age = (now - touch.timestamp).toFloat() / 700f
                val currentAlpha = (1f - age).coerceIn(0f, 1f)

                when (touchStyle) {
                    TouchStyle.RIPPLE -> {
                        drawCircle(
                            color = touchColor.copy(alpha = currentAlpha * 0.7f),
                            radius = radiusPx * (0.8f + age * 1.2f),
                            center = touch.offset,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        drawCircle(
                            color = touchColor.copy(alpha = currentAlpha * 0.4f),
                            radius = radiusPx * 0.6f,
                            center = touch.offset
                        )
                    }
                    TouchStyle.DOT -> {
                        drawCircle(
                            color = touchColor.copy(alpha = currentAlpha),
                            radius = radiusPx * 0.8f,
                            center = touch.offset
                        )
                    }
                    TouchStyle.RING -> {
                        drawCircle(
                            color = touchColor.copy(alpha = currentAlpha),
                            radius = radiusPx,
                            center = touch.offset,
                            style = Stroke(width = 3.5.dp.toPx())
                        )
                    }
                    TouchStyle.FINGER -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(touchColor.copy(alpha = currentAlpha * 0.9f), Color.Transparent),
                                center = touch.offset,
                                radius = radiusPx * 1.5f
                            ),
                            radius = radiusPx * 1.5f,
                            center = touch.offset
                        )
                    }
                }
            }

            // Draw Swipe Gesture Trail
            if (showGestures && gestureTrail.size > 1) {
                for (i in 0 until gestureTrail.size - 1) {
                    val alpha = ((i + 1).toFloat() / gestureTrail.size) * 0.75f
                    drawLine(
                        color = touchColor.copy(alpha = alpha),
                        start = gestureTrail[i],
                        end = gestureTrail[i + 1],
                        strokeWidth = (touchSizeDp * 0.35f) * ((i + 1).toFloat() / gestureTrail.size),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
