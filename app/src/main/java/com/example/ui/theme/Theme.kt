package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAccentTheme = compositionLocalOf { AccentTheme.INDIGO }

@Composable
fun ScreenFlowTheme(
    accentTheme: AccentTheme = AccentTheme.INDIGO,
    content: @Composable () -> Unit
) {
    val lightColors = lightColorScheme(
        primary = accentTheme.primary,
        onPrimary = Color.White,
        primaryContainer = accentTheme.container,
        onPrimaryContainer = accentTheme.onContainer,
        secondary = accentTheme.secondary,
        onSecondary = Color.White,
        secondaryContainer = accentTheme.container,
        onSecondaryContainer = accentTheme.onContainer,
        tertiary = StudioRecordRed,
        onTertiary = Color.White,
        tertiaryContainer = StudioRecordRedContainer,
        onTertiaryContainer = StudioRecordRedDark,
        background = StudioBackgroundLight,
        onBackground = StudioTextPrimary,
        surface = StudioSurfaceLight,
        onSurface = StudioTextPrimary,
        surfaceVariant = StudioBackgroundLight,
        onSurfaceVariant = StudioTextSecondary,
        outline = StudioCardBorder
    )

    CompositionLocalProvider(LocalAccentTheme provides accentTheme) {
        MaterialTheme(
            colorScheme = lightColors,
            typography = Typography,
            content = content
        )
    }
}
