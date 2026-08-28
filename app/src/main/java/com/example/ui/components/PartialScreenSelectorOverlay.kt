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
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PartialAspectRatio
import com.example.data.model.PartialRecordingArea
import com.example.ui.theme.LocalAccentTheme
import kotlin.math.roundToInt

@Composable
fun PartialScreenSelectorOverlay(
    partialArea: PartialRecordingArea,
    onUpdateArea: (PartialRecordingArea) -> Unit,
    onStartPartialRecord: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current
    val density = LocalDensity.current.density

    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("partial_screen_selector")) {
        val screenW = constraints.maxWidth.toFloat()
        val screenH = constraints.maxHeight.toFloat()

        var leftPx by remember { mutableFloatStateOf(screenW * partialArea.xRatio) }
        var topPx by remember { mutableFloatStateOf(screenH * partialArea.yRatio) }
        var rectWPx by remember { mutableFloatStateOf(screenW * partialArea.widthRatio) }
        var rectHPx by remember { mutableFloatStateOf(screenH * partialArea.heightRatio) }
        var selectedRatio by remember { mutableStateOf(partialArea.aspectRatio) }

        val calculatedResW = ((rectWPx / screenW) * 1080).toInt()
        val calculatedResH = ((rectHPx / screenH) * 1920).toInt()

        // Background Dim Canvas with cutout window
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dimColor = Color.Black.copy(alpha = 0.6f)
            val path = Path().apply {
                addRect(Rect(0f, 0f, size.width, size.height))
                addRect(Rect(leftPx, topPx, leftPx + rectWPx, topPx + rectHPx))
                fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
            }
            drawPath(path, color = dimColor, style = Fill)

            // Outer border around selected region
            drawRect(
                color = accent.primary,
                topLeft = Offset(leftPx, topPx),
                size = Size(rectWPx, rectHPx),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Draggable Center Handle for moving selection
        Box(
            modifier = Modifier
                .offset { IntOffset(leftPx.roundToInt(), topPx.roundToInt()) }
                .size(
                    width = (rectWPx / density).dp,
                    height = (rectHPx / density).dp
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        leftPx = (leftPx + dragAmount.x).coerceIn(0f, screenW - rectWPx)
                        topPx = (topPx + dragAmount.y).coerceIn(0f, screenH - rectHPx)
                    }
                }
        ) {
            // Live Resolution Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AspectRatio, contentDescription = null, tint = accent.primary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Selected Area: ${calculatedResW} × ${calculatedResH} px",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                }
            }

            // Bottom-Right Corner Resize Handle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .offset(10.dp, 10.dp)
                    .clip(CircleShape)
                    .background(accent.primary)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            rectWPx = (rectWPx + dragAmount.x).coerceIn(200f, screenW - leftPx)
                            if (selectedRatio.ratio != null) {
                                rectHPx = (rectWPx / selectedRatio.ratio!!).coerceIn(200f, screenH - topPx)
                            } else {
                                rectHPx = (rectHPx + dragAmount.y).coerceIn(200f, screenH - topPx)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AspectRatio, contentDescription = "Resize", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        // Bottom Controls Console
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.96f),
            shadowElevation = 20.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Partial Screen Capture",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Standard Android MediaProjection captures full display and crops target area cleanly.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Aspect Ratio Selector Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PartialAspectRatio.values().forEach { ratio ->
                        val isSelected = selectedRatio == ratio
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) accent.primary else Color.White.copy(alpha = 0.08f),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, if (isSelected) accent.primary else Color.Transparent, RoundedCornerShape(10.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectDragGestures { _, _ -> }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {
                                        selectedRatio = ratio
                                        if (ratio.ratio != null) {
                                            rectHPx = (rectWPx / ratio.ratio!!).coerceIn(200f, screenH - topPx)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) accent.primary else Color.Transparent,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = ratio.label.substringBefore(" "),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Reset / FullScreen / Record Selected
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            leftPx = 0f
                            topPx = 0f
                            rectWPx = screenW
                            rectHPx = screenH
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Full Screen", color = Color.White, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            onUpdateArea(
                                partialArea.copy(
                                    isEnabled = true,
                                    xRatio = leftPx / screenW,
                                    yRatio = topPx / screenH,
                                    widthRatio = rectWPx / screenW,
                                    heightRatio = rectHPx / screenH,
                                    aspectRatio = selectedRatio
                                )
                            )
                            onStartPartialRecord()
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Area", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
