package com.animeblack.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================
// Official Anime Black Design System — AMOLED Black & Cyber Blue Palette
// ============================================================

// AMOLED Black & Dark Surfaces
val AmoledBlack = Color(0xFF050609)     // Background (AMOLED True Black)
val DarkCharcoal = Color(0xFF10121D)    // Surface Primary
val DarkSlate = Color(0xFF181B2B)       // Surface Elevated / Card
val DarkBorder = Color(0x1FFFFFFF)      // 12% Translucent White Border
val DarkBorderFocus = Color(0x4D00A3FF) // 30% Cyber Blue Focus Border

// Brand Accents
val CyberBlue = Color(0xFF00A3FF)       // Primary Accent (Official #00A3FF)
val CyberBlueHover = Color(0xFF33B5FF)  // Primary Hover / Focus
val CyberBlueMuted = Color(0x2600A3FF)  // 15% Cyber Blue Fill

val ElectricPurple = Color(0xFF8A2BE2)  // Secondary Accent
val ElectricPurpleLight = Color(0xFF9D4EDD)
val ElectricPurpleMuted = Color(0x268A2BE2)

// Semantic Status Colors
val StatusSuccess = Color(0xFF10B981)   // Success Emerald
val StatusWarning = Color(0xFFF59E0B)   // Warning Amber
val StatusError = Color(0xFFEF4444)     // Error Rose Red
val StatusInfo = Color(0xFF00A3FF)      // Info Cyber Blue

// Typography Colors (High-Contrast WCAG AA compliant)
val TextPrimary = Color(0xFFF8FAFC)     // 100% White-Slate
val TextSecondary = Color(0xFF94A3B8)   // 70% Muted Slate
val TextMuted = Color(0xFF64748B)       // 50% Dimmed Slate

// Brand Gradients
val GradCyberBlue = Brush.horizontalGradient(listOf(CyberBlue, ElectricPurpleLight))
val GradPurpleToBlue = Brush.verticalGradient(listOf(ElectricPurple, CyberBlue))
val GradDarkSurface = Brush.verticalGradient(listOf(DarkCharcoal, AmoledBlack))
val GradCardOverlay = Brush.verticalGradient(listOf(Color.Transparent, AmoledBlack.copy(alpha = 0.95f)))
