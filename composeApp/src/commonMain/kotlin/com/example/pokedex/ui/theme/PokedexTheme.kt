package com.example.pokedex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2C6E49),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9F3D8),
    onPrimaryContainer = Color(0xFF0D2B18),
    secondary = Color(0xFF1F4AB8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE4FF),
    onSecondaryContainer = Color(0xFF0D1C4F),
    tertiary = Color(0xFFF28F3B),
    onTertiary = Color(0xFF3E2000),
    tertiaryContainer = Color(0xFFFFE0C5),
    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF1B1F24),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1F24),
    surfaceVariant = Color(0xFFE9EEF5),
    onSurfaceVariant = Color(0xFF46515E),
    error = Color(0xFFC83C5A),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8DDBA2),
    onPrimary = Color(0xFF123820),
    primaryContainer = Color(0xFF1D5231),
    onPrimaryContainer = Color(0xFFD9F3D8),
    secondary = Color(0xFFB4C5FF),
    onSecondary = Color(0xFF11245F),
    secondaryContainer = Color(0xFF203A8B),
    onSecondaryContainer = Color(0xFFDCE4FF),
    tertiary = Color(0xFFFFB777),
    onTertiary = Color(0xFF4B2800),
    tertiaryContainer = Color(0xFF704000),
    background = Color(0xFF12161C),
    onBackground = Color(0xFFE7EDF5),
    surface = Color(0xFF171C22),
    onSurface = Color(0xFFE7EDF5),
    surfaceVariant = Color(0xFF27303B),
    onSurfaceVariant = Color(0xFFC4CCD6),
    error = Color(0xFFFF92A6),
    onError = Color(0xFF680019)
)

@Composable
fun PokedexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
