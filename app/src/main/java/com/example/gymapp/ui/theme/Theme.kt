package com.example.gymapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow

// Stan globalny motywu - domyślnie ciemny
object ThemeState {
    val isDark = MutableStateFlow(true)
}

val PastelPink = Color(0xFFF6B6FC)
val MutedBlue = Color(0xFF395294)
val DarkBackground = Color(0xFF121212)
val LightBackground = Color(0xFFFAFAFA)

private val LightColorScheme = lightColorScheme(
    primary = PastelPink,
    background = LightBackground,
    surface = Color.White,
    onPrimary = Color.Black,
    surfaceVariant = Color(0xFFF5E6F7)
)

private val DarkColorScheme = darkColorScheme(
    primary = MutedBlue,
    background = DarkBackground,
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    surfaceVariant = Color(0xFF2A2D35)
)

@Composable
fun GymAppTheme(
    // Wymuszamy czytanie z naszego globalnego stanu zamiast z systemu
    darkTheme: Boolean = ThemeState.isDark.collectAsState().value,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
