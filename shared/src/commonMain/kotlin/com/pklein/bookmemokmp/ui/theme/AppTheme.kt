package com.pklein.bookmemokmp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ── Palette ───────────────────────────────────────────────────────────────────

// Blues
private val Blue600 = Color(0xFF2878B8) // primary
private val Blue100 = Color(0xFFD4E9F7) // primaryContainer
private val Blue900 = Color(0xFF1A3A58) // onPrimaryContainer

// Blue-grays (secondary)
private val BlueGray600 = Color(0xFF4A7EA8)
private val BlueGray100 = Color(0xFFD8E8F4)
private val BlueGray900 = Color(0xFF1A3050)

// Soft teal (tertiary)
private val Teal600 = Color(0xFF3D8A7C)
private val Teal100 = Color(0xFFCFEAE5)
private val Teal900 = Color(0xFF1A3832)

// Surfaces & backgrounds
private val Background = Color(0xFFF2F8FD)
private val Surface = Color(0xFFF8FBFE)
private val SurfaceVariant = Color(0xFFDCE8F2)

// Surface container hierarchy — all blue-tinted, used by elevated components
private val SurfaceContainerLowest = Color(0xFFF6FAFE)
private val SurfaceContainerLow = Color(0xFFF1F7FD)
private val SurfaceContainer = Color(0xFFEBF2FA)
private val SurfaceContainerHigh = Color(0xFFE5EDF7)
private val SurfaceContainerHighest = Color(0xFFDFE8F4)

// Text
private val TextPrimary = Color(0xFF1A2F40)
private val TextSecondary = Color(0xFF4A6478)

// Misc
private val Outline = Color(0xFF9AB8CC)
private val ErrorRed = Color(0xFFCF6679)
private val ErrorRedLight = Color(0xFFF7D9DF)

// Success green (used for "Read" status badge)
val SuccessGreen = Color(0xFF81C784)

// ── Badge colors (used by BookItem) ──────────────────────────────────────────

/** Soft pastel blue — Book badge. */
val BadgeBookColor = Color(0xFF6BA3D0)

/** Soft coral — Manga badge. */
val BadgeMangaColor = Color(0xFFE8956D)

/** Soft teal — Comic badge. */
val BadgeComicColor = Color(0xFF6BBAA8)

// ── Color scheme ──────────────────────────────────────────────────────────────

private val BookMemoColors =
    lightColorScheme(
        primary = Blue600,
        onPrimary = Color.White,
        primaryContainer = Blue100,
        onPrimaryContainer = Blue900,
        secondary = BlueGray600,
        onSecondary = Color.White,
        secondaryContainer = BlueGray100,
        onSecondaryContainer = BlueGray900,
        tertiary = Teal600,
        onTertiary = Color.White,
        tertiaryContainer = Teal100,
        onTertiaryContainer = Teal900,
        background = Background,
        onBackground = TextPrimary,
        surface = Surface,
        onSurface = TextPrimary,
        surfaceVariant = SurfaceVariant,
        onSurfaceVariant = TextSecondary,
        surfaceTint = Blue600,
        surfaceContainerLowest = SurfaceContainerLowest,
        surfaceContainerLow = SurfaceContainerLow,
        surfaceContainer = SurfaceContainer,
        surfaceContainerHigh = SurfaceContainerHigh,
        surfaceContainerHighest = SurfaceContainerHighest,
        outline = Outline,
        error = ErrorRed,
        onError = Color.White,
        errorContainer = ErrorRedLight,
        onErrorContainer = Color(0xFF6B1A2A),
    )

// ── Dark palette ──────────────────────────────────────────────────────────────

// Dark backgrounds — deep navy
private val DarkBackground = Color(0xFF0F1E2D)
private val DarkSurface = Color(0xFF152534)
private val DarkSurfaceVariant = Color(0xFF1E3042)
private val DarkContainerLowest = Color(0xFF0A1520)
private val DarkContainerLow = Color(0xFF0F1E2D)
private val DarkContainer = Color(0xFF152534)
private val DarkContainerHigh = Color(0xFF1B2E3F)
private val DarkContainerHighest = Color(0xFF21384C)

// Primary (lighter blue for contrast on dark)
private val DarkBlue = Color(0xFF8FC4E8)
private val DarkBlueContainer = Color(0xFF1A3E5A)

// Secondary
private val DarkBlueGray = Color(0xFF9AB8D4)
private val DarkBlueGrayContainer = Color(0xFF1A3550)

// Tertiary
private val DarkTeal = Color(0xFF88C4B8)
private val DarkTealContainer = Color(0xFF1A4038)

// Text
private val DarkTextPrimary = Color(0xFFD8EAF5)
private val DarkTextSecondary = Color(0xFF8AACC0)

private val DarkOutline = Color(0xFF4A6880)

// ── Dark color scheme ─────────────────────────────────────────────────────────

private val BookMemoDarkColors =
    darkColorScheme(
        primary = DarkBlue,
        onPrimary = Color(0xFF0A2A40),
        primaryContainer = DarkBlueContainer,
        onPrimaryContainer = Blue100,
        secondary = DarkBlueGray,
        onSecondary = Color(0xFF0A2035),
        secondaryContainer = DarkBlueGrayContainer,
        onSecondaryContainer = BlueGray100,
        tertiary = DarkTeal,
        onTertiary = Color(0xFF0A2820),
        tertiaryContainer = DarkTealContainer,
        onTertiaryContainer = Teal100,
        background = DarkBackground,
        onBackground = DarkTextPrimary,
        surface = DarkSurface,
        onSurface = DarkTextPrimary,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkTextSecondary,
        surfaceTint = DarkBlue,
        surfaceContainerLowest = DarkContainerLowest,
        surfaceContainerLow = DarkContainerLow,
        surfaceContainer = DarkContainer,
        surfaceContainerHigh = DarkContainerHigh,
        surfaceContainerHighest = DarkContainerHighest,
        outline = DarkOutline,
        error = ErrorRed,
        onError = Color(0xFF3E0B1A),
        errorContainer = Color(0xFF6B1A2A),
        onErrorContainer = ErrorRedLight,
    )

// ── Shapes — more rounded for a softer feel ───────────────────────────────────

private val BookMemoShapes =
    Shapes(
        extraSmall = RoundedCornerShape(6.dp),
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(14.dp),
        large = RoundedCornerShape(18.dp),
        extraLarge = RoundedCornerShape(24.dp),
    )

// ── Theme entry point ─────────────────────────────────────────────────────────

@Composable
fun BookMemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) BookMemoDarkColors else BookMemoColors
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = BookMemoShapes,
        content = content,
    )
}
