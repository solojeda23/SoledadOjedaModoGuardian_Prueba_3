package com.example.soledadojedamodoguardian.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8ACDFF),
    onPrimary = Color(0xFF00334F),

    primaryContainer = Color(0xFF17435C),
    onPrimaryContainer = Color(0xFFD4EDFF),

    secondary = Color(0xFF74D9BF),
    onSecondary = Color(0xFF00382D),

    secondaryContainer = Color(0xFF174D40),
    onSecondaryContainer = Color(0xFFB5F3DE),

    tertiary = Color(0xFFEBC17D),
    onTertiary = Color(0xFF412D05),

    background = Color(0xFF0C0E10),
    onBackground = Color(0xFFF0F3F5),

    surface = Color(0xFF131619),
    onSurface = Color(0xFFF0F3F5),

    surfaceVariant = Color(0xFF202529),
    onSurfaceVariant = Color(0xFFBCC7CE),

    surfaceTint = Color(0xFF8ACDFF),

    outline = Color(0xFF89969E),
    outlineVariant = Color(0xFF354047),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),

    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00628D),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFD4EDFF),
    onPrimaryContainer = Color(0xFF002E45),

    secondary = Color(0xFF006B55),
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFB5F3DE),
    onSecondaryContainer = Color(0xFF002117),

    tertiary = Color(0xFF795900),
    onTertiary = Color.White,

    background = Color(0xFFF5F7F8),
    onBackground = Color(0xFF191D20),

    surface = Color(0xFFF5F7F8),
    onSurface = Color(0xFF191D20),

    surfaceVariant = Color(0xFFE0E7EB),
    onSurfaceVariant = Color(0xFF414C53),

    surfaceTint = Color(0xFF00628D),

    outline = Color(0xFF717D85),
    outlineVariant = Color(0xFFC1CCD3),

    error = Color(0xFFBA1A1A),
    onError = Color.White,

    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun SoledadOjedaModoGuardianTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        },
        typography = Typography,
        content = content
    )
}