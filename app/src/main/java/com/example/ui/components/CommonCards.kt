package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurfaceLight
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun SettingGroupCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val accent = LocalAccentTheme.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = StudioSurfaceLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accent.container),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary
                        )
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StudioTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
fun ToggleRow(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = StudioTextPrimary
                )
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = StudioTextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accent.primary,
                uncheckedThumbColor = StudioTextMuted,
                uncheckedTrackColor = StudioCardBorder
            )
        )
    }
}

@Composable
fun SpecSelectorRow(
    title: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = StudioTextSecondary
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) accent.primary else accent.container.copy(alpha = 0.5f))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) accent.primary else StudioCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onOptionSelected(option) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else StudioTextPrimary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun StorageBarIndicator(
    freeBytes: Long,
    appBytes: Long,
    screenshotBytes: Long,
    modifier: Modifier = Modifier
) {
    val accent = LocalAccentTheme.current
    val totalEstimated = freeBytes + appBytes + screenshotBytes
    val appFraction = if (totalEstimated > 0) (appBytes.toFloat() / totalEstimated).coerceIn(0.01f, 1f) else 0.05f
    val screenshotFraction = if (totalEstimated > 0) (screenshotBytes.toFloat() / totalEstimated).coerceIn(0.01f, 1f) else 0.02f

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFE2E8F0))
        ) {
            // App videos
            Box(
                modifier = Modifier
                    .weight(appFraction)
                    .height(10.dp)
                    .background(accent.primary)
            )
            // Screenshots
            Box(
                modifier = Modifier
                    .weight(screenshotFraction)
                    .height(10.dp)
                    .background(Color(0xFF06B6D4))
            )
            // Free storage
            Box(
                modifier = Modifier
                    .weight((1f - appFraction - screenshotFraction).coerceAtLeast(0.01f))
                    .height(10.dp)
                    .background(Color(0xFFE2E8F0))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accent.primary))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Videos", style = MaterialTheme.typography.labelSmall.copy(color = StudioTextSecondary))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF06B6D4)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Screenshots", style = MaterialTheme.typography.labelSmall.copy(color = StudioTextSecondary))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF94A3B8)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Free Space", style = MaterialTheme.typography.labelSmall.copy(color = StudioTextSecondary))
            }
        }
    }
}
