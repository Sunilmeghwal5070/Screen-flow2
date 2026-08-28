package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FloatingIconAnimation
import com.example.data.model.FloatingIconConfig
import com.example.data.model.FloatingIconShape
import com.example.data.model.GestureAction
import com.example.data.model.GestureShortcutsConfig
import com.example.data.model.PanelAction
import com.example.data.model.SmartPanelLayoutMode
import com.example.ui.components.SettingGroupCard
import com.example.ui.components.SpecSelectorRow
import com.example.ui.components.ToggleRow
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioRecordRed
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FloatingStudioScreen(
    iconConfig: FloatingIconConfig,
    panelLayoutMode: SmartPanelLayoutMode,
    customActions: List<PanelAction>,
    gestureConfig: GestureShortcutsConfig,
    onUpdateIconConfig: (FloatingIconConfig) -> Unit,
    onUpdatePanelMode: (SmartPanelLayoutMode) -> Unit,
    onToggleCustomAction: (PanelAction) -> Unit,
    onUpdateGestureConfig: (GestureShortcutsConfig) -> Unit,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("floating_studio_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Floating Smart Panel Studio",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = StudioTextPrimary
                        )
                    )
                    Text(
                        text = "Customize your draggable floating bubble, smart control grid, edge-snap magnetic margins, and gesture shortcuts.",
                        style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary)
                    )
                }
                if (onClose != null) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StudioTextPrimary)
                    }
                }
            }
        }

        // 1. Live Interactive Bubble Preview Box
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, accent.primary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LIVE FLOATING BUBBLE PREVIEW",
                        color = accent.primary,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Preview Bubble Display
                    Surface(
                        shape = when (iconConfig.shape) {
                            FloatingIconShape.CIRCLE -> CircleShape
                            FloatingIconShape.ROUNDED_SQUARE -> RoundedCornerShape(14.dp)
                            FloatingIconShape.PILL -> RoundedCornerShape(24.dp)
                            FloatingIconShape.MINIMAL_DOT -> CircleShape
                        },
                        color = Color(0xFF1E293B).copy(alpha = iconConfig.opacity),
                        shadowElevation = if (iconConfig.hasShadow) 12.dp else 0.dp,
                        border = if (iconConfig.hasBorder) androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (iconConfig.showRecDot) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(StudioRecordRed)
                                )
                            }
                            if (iconConfig.showTimer && iconConfig.shape != FloatingIconShape.MINIMAL_DOT) {
                                Text(
                                    text = "00:04:18",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Shape: ${iconConfig.shape.label} · Opacity: ${(iconConfig.opacity * 100).toInt()}% · Animation: ${iconConfig.animation.label}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 2. Floating Icon Appearance Controls
        item {
            SettingGroupCard(
                title = "Floating Bubble Appearance",
                subtitle = "Shape, size scaling, and animation style",
                icon = Icons.Default.Widgets
            ) {
                // Shape Selector
                SpecSelectorRow(
                    title = "Bubble Shape",
                    selectedOption = iconConfig.shape.label,
                    options = FloatingIconShape.values().map { it.label },
                    onOptionSelected = { label ->
                        val shape = FloatingIconShape.values().firstOrNull { it.label == label } ?: FloatingIconShape.PILL
                        onUpdateIconConfig(iconConfig.copy(shape = shape))
                    }
                )

                // Animation Selector
                SpecSelectorRow(
                    title = "Indicator Animation",
                    selectedOption = iconConfig.animation.label,
                    options = FloatingIconAnimation.values().map { it.label },
                    onOptionSelected = { label ->
                        val anim = FloatingIconAnimation.values().firstOrNull { it.label == label } ?: FloatingIconAnimation.PULSE
                        onUpdateIconConfig(iconConfig.copy(animation = anim))
                    }
                )

                // Opacity Slider
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bubble Opacity", style = MaterialTheme.typography.bodySmall.copy(color = StudioTextSecondary))
                        Text("${(iconConfig.opacity * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = accent.primary))
                    }
                    Slider(
                        value = iconConfig.opacity,
                        onValueChange = { onUpdateIconConfig(iconConfig.copy(opacity = it)) },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = accent.primary, activeTrackColor = accent.primary)
                    )
                }

                // Size Scale (Small, Medium, Large)
                SpecSelectorRow(
                    title = "Icon Sizing",
                    selectedOption = when (iconConfig.sizeScale) {
                        0.8f -> "Small (44dp)"
                        1.25f -> "Large (64dp)"
                        else -> "Medium (54dp)"
                    },
                    options = listOf("Small (44dp)", "Medium (54dp)", "Large (64dp)"),
                    onOptionSelected = {
                        val scale = when (it) {
                            "Small (44dp)" -> 0.8f
                            "Large (64dp)" -> 1.25f
                            else -> 1.0f
                        }
                        onUpdateIconConfig(iconConfig.copy(sizeScale = scale))
                    }
                )

                ToggleRow(
                    title = "High Contrast Border",
                    description = "Adds a crisp border line for dark and bright backgrounds",
                    checked = iconConfig.hasBorder,
                    onCheckedChange = { onUpdateIconConfig(iconConfig.copy(hasBorder = it)) }
                )

                ToggleRow(
                    title = "Subtle Elevation Shadow",
                    description = "Elevates bubble above recorded screen elements",
                    checked = iconConfig.hasShadow,
                    onCheckedChange = { onUpdateIconConfig(iconConfig.copy(hasShadow = it)) }
                )

                ToggleRow(
                    title = "Display Realtime Timer",
                    description = "Shows live MM:SS recording elapsed duration",
                    checked = iconConfig.showTimer,
                    onCheckedChange = { onUpdateIconConfig(iconConfig.copy(showTimer = it)) }
                )
            }
        }

        // 3. Magnetic Edge-Snapping & Position Behavior
        item {
            SettingGroupCard(
                title = "Magnetic Edge-Snapping & Margin",
                subtitle = "Smooth magnetic attraction when near screen borders",
                icon = Icons.Default.DragHandle
            ) {
                ToggleRow(
                    title = "Snap to Screen Edges",
                    description = "Automatically snaps to left or right screen edge when released",
                    checked = iconConfig.edgeSnap,
                    onCheckedChange = { onUpdateIconConfig(iconConfig.copy(edgeSnap = it)) }
                )

                SpecSelectorRow(
                    title = "Edge Margin Spacing",
                    selectedOption = "${iconConfig.edgeMarginDp} dp",
                    options = listOf("8 dp", "12 dp", "18 dp", "24 dp"),
                    onOptionSelected = {
                        val margin = it.replace(" dp", "").toIntOrNull() ?: 12
                        onUpdateIconConfig(iconConfig.copy(edgeMarginDp = margin))
                    }
                )

                ToggleRow(
                    title = "Remember Last Position",
                    description = "Starts subsequent recordings at the exact dragged screen coordinates",
                    checked = iconConfig.rememberPosition,
                    onCheckedChange = { onUpdateIconConfig(iconConfig.copy(rememberPosition = it)) }
                )

                SpecSelectorRow(
                    title = "Smart Panel Auto-Collapse",
                    selectedOption = when (iconConfig.autoHideSeconds) {
                        3 -> "3 Seconds"
                        5 -> "5 Seconds"
                        10 -> "10 Seconds"
                        else -> "Always Visible"
                    },
                    options = listOf("Always Visible", "3 Seconds", "5 Seconds", "10 Seconds"),
                    onOptionSelected = {
                        val sec = when (it) {
                            "3 Seconds" -> 3
                            "5 Seconds" -> 5
                            "10 Seconds" -> 10
                            else -> 0
                        }
                        onUpdateIconConfig(iconConfig.copy(autoHideSeconds = sec))
                    }
                )
            }
        }

        // 4. Smart Panel Layout Mode
        item {
            SettingGroupCard(
                title = "Smart Panel Layout Mode",
                subtitle = "Choose standard, compact, gaming, tutorial, or custom action grids",
                icon = Icons.Default.Dashboard
            ) {
                SmartPanelLayoutMode.values().forEach { mode ->
                    val isSelected = mode == panelLayoutMode
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onUpdatePanelMode(mode) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) accent.container else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) accent.primary else StudioCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) accent.primary else Color(0xFFE2E8F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (mode) {
                                        SmartPanelLayoutMode.GAMING -> Icons.Default.SportsEsports
                                        SmartPanelLayoutMode.TUTORIAL -> Icons.Default.TouchApp
                                        SmartPanelLayoutMode.COMPACT -> Icons.Default.CropSquare
                                        SmartPanelLayoutMode.STANDARD -> Icons.Default.Dashboard
                                        SmartPanelLayoutMode.EXPANDED -> Icons.Default.Widgets
                                        SmartPanelLayoutMode.CUSTOM -> Icons.Default.Tune
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) accent.primary else StudioTextPrimary
                                    )
                                )
                                Text(
                                    text = mode.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = StudioTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Custom Panel Action Builder (When in Custom Mode)
        if (panelLayoutMode == SmartPanelLayoutMode.CUSTOM) {
            item {
                SettingGroupCard(
                    title = "Custom Panel Actions",
                    subtitle = "Tap actions to include or exclude from your floating console",
                    icon = Icons.Default.Tune
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PanelAction.values().forEach { action ->
                            val isIncluded = customActions.contains(action)
                            FilterChip(
                                selected = isIncluded,
                                onClick = { onToggleCustomAction(action) },
                                label = { Text(action.label, fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(action.defaultIcon, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accent.container,
                                    selectedLabelColor = accent.primary,
                                    selectedLeadingIconColor = accent.primary
                                )
                            )
                        }
                    }
                }
            }
        }

        // 6. Gesture Shortcuts & Custom Actions
        item {
            SettingGroupCard(
                title = "Gesture Shortcuts & Triggers",
                subtitle = "Execute instant actions without opening the full panel",
                icon = Icons.Default.Gesture
            ) {
                // Double Tap
                SpecSelectorRow(
                    title = "Double Tap Bubble",
                    selectedOption = gestureConfig.doubleTap.label,
                    options = listOf(GestureAction.PAUSE_RESUME.label, GestureAction.SCREENSHOT.label, GestureAction.PRIVACY_SHIELD.label, GestureAction.NONE.label),
                    onOptionSelected = { label ->
                        val act = GestureAction.values().firstOrNull { it.label == label } ?: GestureAction.PAUSE_RESUME
                        onUpdateGestureConfig(gestureConfig.copy(doubleTap = act))
                    }
                )

                // Long Press
                SpecSelectorRow(
                    title = "Long Press Bubble",
                    selectedOption = gestureConfig.longPress.label,
                    options = listOf(GestureAction.OPEN_PANEL.label, GestureAction.CONTROL_LOCK.label, GestureAction.EMERGENCY_STOP.label, GestureAction.NONE.label),
                    onOptionSelected = { label ->
                        val act = GestureAction.values().firstOrNull { it.label == label } ?: GestureAction.OPEN_PANEL
                        onUpdateGestureConfig(gestureConfig.copy(longPress = act))
                    }
                )

                // Swipe Up
                SpecSelectorRow(
                    title = "Swipe Up on Bubble",
                    selectedOption = gestureConfig.swipeUp.label,
                    options = listOf(GestureAction.SCREENSHOT.label, GestureAction.DRAW.label, GestureAction.PRIVACY_SHIELD.label, GestureAction.NONE.label),
                    onOptionSelected = { label ->
                        val act = GestureAction.values().firstOrNull { it.label == label } ?: GestureAction.SCREENSHOT
                        onUpdateGestureConfig(gestureConfig.copy(swipeUp = act))
                    }
                )
            }
        }
    }
}
