package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioRecordRed
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class AnnotationTool {
    PEN,
    HIGHLIGHTER,
    ARROW,
    RECTANGLE,
    CIRCLE,
    TEXT,
    ERASER
}

data class DrawnShape(
    val tool: AnnotationTool,
    val points: List<Offset>,
    val start: Offset = Offset.Zero,
    val end: Offset = Offset.Zero,
    val color: Color,
    val strokeWidth: Float,
    val text: String = ""
)

@Composable
fun ScreenAnnotationOverlay(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current

    var activeTool by remember { mutableStateOf(AnnotationTool.PEN) }
    var selectedColor by remember { mutableStateOf(Color(0xFFEF4444)) }
    var strokeWidth by remember { mutableFloatStateOf(8f) }

    val shapes = remember { mutableStateListOf<DrawnShape>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var dragStart by remember { mutableStateOf<Offset?>(null) }
    var dragEnd by remember { mutableStateOf<Offset?>(null) }

    val colorPresets = listOf(
        Color(0xFFEF4444), // Red
        Color(0xFF3B82F6), // Blue
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Amber
        Color(0xFF8B5CF6), // Purple
        Color(0xFFFFFFFF), // White
        Color(0xFF000000)  // Black
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.25f))
            .testTag("screen_annotation_overlay")
    ) {
        // Drawing Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(activeTool, selectedColor, strokeWidth) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragStart = offset
                            dragEnd = offset
                            currentPoints = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            dragEnd = change.position
                            if (activeTool == AnnotationTool.PEN || activeTool == AnnotationTool.HIGHLIGHTER || activeTool == AnnotationTool.ERASER) {
                                currentPoints = currentPoints + change.position
                            }
                        },
                        onDragEnd = {
                            val start = dragStart ?: return@detectDragGestures
                            val end = dragEnd ?: start

                            if (activeTool == AnnotationTool.PEN || activeTool == AnnotationTool.HIGHLIGHTER) {
                                shapes.add(
                                    DrawnShape(
                                        tool = activeTool,
                                        points = currentPoints,
                                        color = if (activeTool == AnnotationTool.HIGHLIGHTER) selectedColor.copy(alpha = 0.4f) else selectedColor,
                                        strokeWidth = if (activeTool == AnnotationTool.HIGHLIGHTER) strokeWidth * 2.5f else strokeWidth
                                    )
                                )
                            } else if (activeTool == AnnotationTool.ARROW || activeTool == AnnotationTool.RECTANGLE || activeTool == AnnotationTool.CIRCLE) {
                                shapes.add(
                                    DrawnShape(
                                        tool = activeTool,
                                        points = emptyList(),
                                        start = start,
                                        end = end,
                                        color = selectedColor,
                                        strokeWidth = strokeWidth
                                    )
                                )
                            } else if (activeTool == AnnotationTool.TEXT) {
                                shapes.add(
                                    DrawnShape(
                                        tool = activeTool,
                                        points = emptyList(),
                                        start = start,
                                        color = selectedColor,
                                        strokeWidth = strokeWidth,
                                        text = "Annotation"
                                    )
                                )
                            }
                            currentPoints = emptyList()
                            dragStart = null
                            dragEnd = null
                        }
                    )
                }
        ) {
            // Draw stored shapes
            shapes.forEach { shape ->
                when (shape.tool) {
                    AnnotationTool.PEN, AnnotationTool.HIGHLIGHTER -> {
                        if (shape.points.size > 1) {
                            val path = Path().apply {
                                moveTo(shape.points.first().x, shape.points.first().y)
                                for (i in 1 until shape.points.size) {
                                    lineTo(shape.points[i].x, shape.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = shape.color,
                                style = Stroke(
                                    width = shape.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                    AnnotationTool.RECTANGLE -> {
                        val topLeft = Offset(
                            minOf(shape.start.x, shape.end.x),
                            minOf(shape.start.y, shape.end.y)
                        )
                        val size = Size(
                            kotlin.math.abs(shape.end.x - shape.start.x),
                            kotlin.math.abs(shape.end.y - shape.start.y)
                        )
                        drawRect(
                            color = shape.color,
                            topLeft = topLeft,
                            size = size,
                            style = Stroke(width = shape.strokeWidth)
                        )
                    }
                    AnnotationTool.CIRCLE -> {
                        val center = Offset(
                            (shape.start.x + shape.end.x) / 2,
                            (shape.start.y + shape.end.y) / 2
                        )
                        val radius = (Offset(shape.end.x - shape.start.x, shape.end.y - shape.start.y).getDistance() / 2)
                        drawCircle(
                            color = shape.color,
                            center = center,
                            radius = radius,
                            style = Stroke(width = shape.strokeWidth)
                        )
                    }
                    AnnotationTool.ARROW -> {
                        drawLine(
                            color = shape.color,
                            start = shape.start,
                            end = shape.end,
                            strokeWidth = shape.strokeWidth,
                            cap = StrokeCap.Round
                        )
                        // Arrow head
                        val angle = atan2((shape.end.y - shape.start.y).toDouble(), (shape.end.x - shape.start.x).toDouble())
                        val arrowLen = shape.strokeWidth * 3.5f
                        val arrowHead1 = Offset(
                            (shape.end.x - arrowLen * cos(angle - Math.PI / 6)).toFloat(),
                            (shape.end.y - arrowLen * sin(angle - Math.PI / 6)).toFloat()
                        )
                        val arrowHead2 = Offset(
                            (shape.end.x - arrowLen * cos(angle + Math.PI / 6)).toFloat(),
                            (shape.end.y - arrowLen * sin(angle + Math.PI / 6)).toFloat()
                        )
                        drawLine(shape.color, shape.end, arrowHead1, strokeWidth = shape.strokeWidth, cap = StrokeCap.Round)
                        drawLine(shape.color, shape.end, arrowHead2, strokeWidth = shape.strokeWidth, cap = StrokeCap.Round)
                    }
                    else -> {}
                }
            }

            // Draw current active stroke
            if (currentPoints.size > 1 && (activeTool == AnnotationTool.PEN || activeTool == AnnotationTool.HIGHLIGHTER)) {
                val path = Path().apply {
                    moveTo(currentPoints.first().x, currentPoints.first().y)
                    for (i in 1 until currentPoints.size) {
                        lineTo(currentPoints[i].x, currentPoints[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = if (activeTool == AnnotationTool.HIGHLIGHTER) selectedColor.copy(alpha = 0.4f) else selectedColor,
                    style = Stroke(
                        width = if (activeTool == AnnotationTool.HIGHLIGHTER) strokeWidth * 2.5f else strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            } else if (dragStart != null && dragEnd != null) {
                val s = dragStart!!
                val e = dragEnd!!
                when (activeTool) {
                    AnnotationTool.RECTANGLE -> {
                        val topLeft = Offset(minOf(s.x, e.x), minOf(s.y, e.y))
                        val size = Size(kotlin.math.abs(e.x - s.x), kotlin.math.abs(e.y - s.y))
                        drawRect(selectedColor, topLeft, size, style = Stroke(width = strokeWidth))
                    }
                    AnnotationTool.CIRCLE -> {
                        val center = Offset((s.x + e.x) / 2, (s.y + e.y) / 2)
                        val radius = (Offset(e.x - s.x, e.y - s.y).getDistance() / 2)
                        drawCircle(selectedColor, radius, center, style = Stroke(width = strokeWidth))
                    }
                    AnnotationTool.ARROW -> {
                        drawLine(selectedColor, s, e, strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    }
                    else -> {}
                }
            }
        }

        // Top Header with Close and Undo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.75f),
                shadowElevation = 6.dp
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close Drawing", tint = Color.White)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.75f),
                    shadowElevation = 6.dp
                ) {
                    IconButton(
                        onClick = { if (shapes.isNotEmpty()) shapes.removeAt(shapes.lastIndex) }
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = Color.White)
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.75f),
                    shadowElevation = 6.dp
                ) {
                    IconButton(onClick = { shapes.clear() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Canvas", tint = StudioRecordRed)
                    }
                }
            }
        }

        // Bottom Floating Toolbar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tool Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolIconItem(
                        icon = Icons.Default.Edit,
                        isSelected = activeTool == AnnotationTool.PEN,
                        onClick = { activeTool = AnnotationTool.PEN }
                    )
                    ToolIconItem(
                        icon = Icons.Default.Highlight,
                        isSelected = activeTool == AnnotationTool.HIGHLIGHTER,
                        onClick = { activeTool = AnnotationTool.HIGHLIGHTER }
                    )
                    ToolIconItem(
                        icon = Icons.Default.ArrowForward,
                        isSelected = activeTool == AnnotationTool.ARROW,
                        onClick = { activeTool = AnnotationTool.ARROW }
                    )
                    ToolIconItem(
                        icon = Icons.Default.CropSquare,
                        isSelected = activeTool == AnnotationTool.RECTANGLE,
                        onClick = { activeTool = AnnotationTool.RECTANGLE }
                    )
                    ToolIconItem(
                        icon = Icons.Default.RadioButtonUnchecked,
                        isSelected = activeTool == AnnotationTool.CIRCLE,
                        onClick = { activeTool = AnnotationTool.CIRCLE }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Color Palette Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colorPresets.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == color) 3.dp else 1.dp,
                                    color = if (selectedColor == color) accent.primary else Color(0xFFE2E8F0),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == color) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (color == Color.White) Color.Black else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolIconItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accent = LocalAccentTheme.current

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accent.container else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) accent.primary else Color(0xFF64748B),
            modifier = Modifier.size(22.dp)
        )
    }
}
