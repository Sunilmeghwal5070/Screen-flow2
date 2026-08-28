package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioTextSecondary

@Composable
fun AudioVUMeter(
    amplitude: Float, // 0.0 to 1.0
    barCount: Int = 18,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current

    Row(
        modifier = modifier
            .height(28.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val threshold = (i + 1).toFloat() / barCount
            val isActive = amplitude >= threshold * 0.7f

            val targetHeight = if (isActive) {
                (12 + (i * 1.0f)).coerceAtMost(26f)
            } else {
                6f
            }

            val animatedHeight by animateFloatAsState(
                targetValue = targetHeight,
                animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing),
                label = "bar_height_$i"
            )

            val barColor = when {
                i > (barCount * 0.8f) -> StudioRecordRed
                i > (barCount * 0.6f) -> Color(0xFFF59E0B) // Amber warning
                isActive -> accent.primary
                else -> Color(0xFFE2E8F0)
            }

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(animatedHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}
