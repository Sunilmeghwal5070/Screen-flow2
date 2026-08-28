package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrivacyZone
import com.example.data.model.PrivacyZoneType
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioRecordRed
import kotlin.math.roundToInt

@Composable
fun PrivacyShieldOverlay(
    zones: List<PrivacyZone>,
    isEditMode: Boolean,
    onAddZone: (PrivacyZone) -> Unit,
    onUpdateZone: (PrivacyZone) -> Unit,
    onDeleteZone: (String) -> Unit,
    onCloseEditMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current
    val density = LocalDensity.current.density

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("privacy_shield_overlay")
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Render each privacy zone
        zones.forEach { zone ->
            val zoneLeftPx = zone.x * widthPx
            val zoneTopPx = zone.y * heightPx
            val zoneWidthPx = zone.width * widthPx
            val zoneHeightPx = zone.height * heightPx

            var dragX by remember(zone.id) { mutableStateOf(zoneLeftPx) }
            var dragY by remember(zone.id) { mutableStateOf(zoneTopPx) }
            var currentW by remember(zone.id) { mutableStateOf(zoneWidthPx) }
            var currentH by remember(zone.id) { mutableStateOf(zoneHeightPx) }

            Box(
                modifier = Modifier
                    .offset { IntOffset(dragX.roundToInt(), dragY.roundToInt()) }
                    .size(
                        width = (currentW / density).dp.coerceAtLeast(60.dp),
                        height = (currentH / density).dp.coerceAtLeast(40.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (isEditMode) {
                            Modifier.pointerInput(zone.id) {
                                detectDragGestures(
                                    onDragEnd = {
                                        onUpdateZone(
                                            zone.copy(
                                                x = (dragX / widthPx).coerceIn(0f, 0.9f),
                                                y = (dragY / heightPx).coerceIn(0f, 0.9f),
                                                width = (currentW / widthPx).coerceIn(0.1f, 0.9f),
                                                height = (currentH / heightPx).coerceIn(0.05f, 0.9f)
                                            )
                                        )
                                    }
                                ) { change, dragAmount ->
                                    change.consume()
                                    dragX = (dragX + dragAmount.x).coerceIn(0f, widthPx - currentW)
                                    dragY = (dragY + dragAmount.y).coerceIn(0f, heightPx - currentH)
                                }
                            }
                        } else Modifier
                    )
            ) {
                // Zone Mask Rendering (Blur mosaic, Pixelate grid, or Solid shield)
                when (zone.type) {
                    PrivacyZoneType.BLUR -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                color = Color(0xFF1E293B).copy(alpha = zone.opacity),
                                size = size
                            )
                            // Simulated Gaussian blur noise lines
                            val step = 12f
                            for (i in 0 until (size.width / step).toInt()) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.12f),
                                    start = Offset(i * step, 0f),
                                    end = Offset(i * step, size.height),
                                    strokeWidth = 2f
                                )
                            }
                            for (j in 0 until (size.height / step).toInt()) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.12f),
                                    start = Offset(0f, j * step),
                                    end = Offset(size.width, j * step),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }
                    PrivacyZoneType.PIXELATE -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cellSize = 16f
                            var x = 0f
                            while (x < size.width) {
                                var y = 0f
                                while (y < size.height) {
                                    val shade = if (((x / cellSize).toInt() + (y / cellSize).toInt()) % 2 == 0) 0.88f else 0.72f
                                    drawRect(
                                        color = Color(0xFF0F172A).copy(alpha = shade * zone.opacity),
                                        topLeft = Offset(x, y),
                                        size = Size(cellSize, cellSize)
                                    )
                                    y += cellSize
                                }
                                x += cellSize
                            }
                        }
                    }
                    PrivacyZoneType.SOLID_COVER -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0F172A).copy(alpha = zone.opacity))
                        )
                    }
                }

                // Edit Handles & Label
                if (isEditMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                    ) {
                        // Label badge
                        Surface(
                            shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                            color = Color(0xFF10B981),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = zone.type.label.substringBefore(" "),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Delete button
                        IconButton(
                            onClick = { onDeleteZone(zone.id) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .background(StudioRecordRed, CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Delete Zone", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // Active Privacy Shield Top Indicator
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF065F46).copy(alpha = 0.92f),
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Privacy Shield Active (${zones.size} Zones)",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            }
        }

        // Edit Mode Toolbar Bar (Bottom)
        if (isEditMode) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(0.92f),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.96f),
                shadowElevation = 16.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Privacy Zone Studio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        IconButton(onClick = onCloseEditMode) {
                            Icon(Icons.Default.Check, contentDescription = "Done", tint = Color(0xFF10B981))
                        }
                    }

                    Text(
                        text = "Drag rectangles over sensitive data like OTPs, passwords, bank cards, or phone numbers.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onAddZone(
                                    PrivacyZone(
                                        id = "zone_${System.currentTimeMillis()}",
                                        x = 0.2f,
                                        y = 0.4f,
                                        width = 0.6f,
                                        height = 0.12f,
                                        type = PrivacyZoneType.BLUR
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Icon(Icons.Default.BlurOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Blur Zone", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onAddZone(
                                    PrivacyZone(
                                        id = "zone_${System.currentTimeMillis()}",
                                        x = 0.2f,
                                        y = 0.55f,
                                        width = 0.6f,
                                        height = 0.12f,
                                        type = PrivacyZoneType.PIXELATE
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Pixelate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onAddZone(
                                    PrivacyZone(
                                        id = "zone_${System.currentTimeMillis()}",
                                        x = 0.2f,
                                        y = 0.25f,
                                        width = 0.6f,
                                        height = 0.12f,
                                        type = PrivacyZoneType.SOLID_COVER
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569))
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Solid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
