package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Neutral Base Colors
val StudioBackgroundLight = Color(0xFFF8FAFC)
val StudioSurfaceLight = Color(0xFFFFFFFF)
val StudioSurfaceElevated = Color(0xFFFFFFFF)
val StudioCardBorder = Color(0xFFE2E8F0)
val StudioTextPrimary = Color(0xFF0F172A)
val StudioTextSecondary = Color(0xFF64748B)
val StudioTextMuted = Color(0xFF94A3B8)

val StudioRecordRed = Color(0xFFEF4444)
val StudioRecordRedDark = Color(0xFFDC2626)
val StudioRecordRedContainer = Color(0xFFFEE2E2)

// Accent Themes
enum class AccentTheme(
    val id: String,
    val title: String,
    val primary: Color,
    val secondary: Color,
    val container: Color,
    val onContainer: Color
) {
    INDIGO(
        "Indigo",
        "Electric Indigo",
        Color(0xFF4F46E5),
        Color(0xFF818CF8),
        Color(0xFFEEF2FF),
        Color(0xFF312E81)
    ),
    VIOLET(
        "Violet",
        "Deep Violet",
        Color(0xFF7C3AED),
        Color(0xFFA78BFA),
        Color(0xFFF5F3FF),
        Color(0xFF4C1D95)
    ),
    MINT(
        "Mint",
        "Fresh Mint",
        Color(0xFF059669),
        Color(0xFF34D399),
        Color(0xFFECFDF5),
        Color(0xFF064E3B)
    ),
    CORAL(
        "Coral",
        "Sunset Coral",
        Color(0xFFE11D48),
        Color(0xFFFB7185),
        Color(0xFFFFF1F2),
        Color(0xFF881337)
    ),
    AMBER(
        "Amber",
        "Golden Amber",
        Color(0xFFD97706),
        Color(0xFFFBBF24),
        Color(0xFFFFFBEB),
        Color(0xFF78350F)
    ),
    CYAN(
        "Cyan",
        "Electric Cyan",
        Color(0xFF0284C7),
        Color(0xFF38BDF8),
        Color(0xFFF0F9FF),
        Color(0xFF0C4A6E)
    ),
    ROSE(
        "Rose",
        "Velvet Rose",
        Color(0xFFDB2777),
        Color(0xFFF472B6),
        Color(0xFFFDF2F8),
        Color(0xFF831843)
    )
}
