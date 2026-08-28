package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.viewmodel.TouchStyle
import kotlinx.coroutines.launch

data class TouchRippleEvent(
    val position: Offset,
    val radius: Animatable<Float, *>,
    val alpha: Animatable<Float, *>
)

@Composable
fun TouchRippleCanvas(
    touchStyle: TouchStyle = TouchStyle.RIPPLE,
    touchColor: Color? = null,
    baseSizeDp: Int = 48,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current
    val effectiveColor = touchColor ?: accent.primary
    val ripples = remember { mutableStateListOf<TouchRippleEvent>() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val radiusAnim = Animatable(10f)
                    val alphaAnim = Animatable(0.9f)
                    val event = TouchRippleEvent(offset, radiusAnim, alphaAnim)
                    ripples.add(event)

                    scope.launch {
                        launch {
                            radiusAnim.animateTo(
                                targetValue = baseSizeDp * 2.2f,
                                animationSpec = tween(400, easing = LinearOutSlowInEasing)
                            )
                        }
                        launch {
                            alphaAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(400, easing = FastOutLinearInEasing)
                            )
                        }.invokeOnCompletion {
                            ripples.remove(event)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            ripples.forEach { event ->
                val currentRadius = event.radius.value
                val currentAlpha = event.alpha.value

                when (touchStyle) {
                    TouchStyle.RIPPLE -> {
                        drawCircle(
                            color = effectiveColor.copy(alpha = currentAlpha * 0.3f),
                            radius = currentRadius,
                            center = event.position
                        )
                        drawCircle(
                            color = effectiveColor.copy(alpha = currentAlpha),
                            radius = currentRadius,
                            center = event.position,
                            style = Stroke(width = 3f)
                        )
                    }
                    TouchStyle.DOT -> {
                        drawCircle(
                            color = effectiveColor.copy(alpha = currentAlpha),
                            radius = (baseSizeDp / 2.5f),
                            center = event.position
                        )
                    }
                    TouchStyle.RING -> {
                        drawCircle(
                            color = effectiveColor.copy(alpha = currentAlpha),
                            radius = currentRadius,
                            center = event.position,
                            style = Stroke(width = 5f)
                        )
                    }
                    TouchStyle.FINGER -> {
                        drawCircle(
                            color = effectiveColor.copy(alpha = currentAlpha * 0.4f),
                            radius = baseSizeDp.toFloat(),
                            center = event.position
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = currentAlpha),
                            radius = 16f,
                            center = event.position
                        )
                    }
                }
            }
        }
    }
}
