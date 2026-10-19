package com.animeblack.app.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat

// ============================================================
// Official Material 3 Dark Color Scheme for Anime Black
// ============================================================

private val AnimeBlackDarkColorScheme = darkColorScheme(
    primary = CyberBlue,
    onPrimary = AmoledBlack,
    primaryContainer = CyberBlueMuted,
    onPrimaryContainer = CyberBlue,

    secondary = ElectricPurple,
    onSecondary = TextPrimary,
    secondaryContainer = ElectricPurpleMuted,
    onSecondaryContainer = ElectricPurpleLight,

    tertiary = StatusInfo,
    onTertiary = AmoledBlack,

    background = AmoledBlack,
    onBackground = TextPrimary,

    surface = DarkCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = DarkSlate,
    onSurfaceVariant = TextSecondary,

    error = StatusError,
    onError = TextPrimary,

    outline = DarkBorder,
    outlineVariant = DarkBorderFocus
)

@Composable
fun AnimeBlackTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = AnimeBlackDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AmoledBlack.toArgb()
            window.navigationBarColor = AmoledBlack.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    // Enforce RTL layout direction for Arabic
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AnimeBlackTypography,
            shapes = AnimeBlackShapes,
            content = content
        )
    }
}
