package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    DARK,
    AMOLED,
    LIGHT
}

private val DarkColorScheme = darkColorScheme(
    primary = CyanWave,
    onPrimary = DeepObsidian,
    primaryContainer = IndigoWave,
    onPrimaryContainer = Color.White,
    secondary = AmberWave,
    onSecondary = DeepObsidian,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = CoralAccent,
    background = DeepObsidian,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder
)

private val AmoledColorScheme = darkColorScheme(
    primary = CyanWave,
    onPrimary = Color.Black,
    primaryContainer = IndigoWave,
    onPrimaryContainer = Color.White,
    secondary = AmberWave,
    onSecondary = Color.Black,
    secondaryContainer = AmoledSurfaceVariant,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = CoralAccent,
    background = AmoledBlack,
    onBackground = TextPrimaryDark,
    surface = AmoledSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = AmoledBorder
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoWave,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = IndigoWave,
    secondary = AmberWave,
    onSecondary = Color.Black,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = CoralAccent,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.AMOLED -> AmoledColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
