package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NafasDarkColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Color(0xFF2A1500),
    primaryContainer = AmberDark,
    onPrimaryContainer = AmberGlow,
    secondary = CalmAmber,
    onSecondary = Color(0xFF231709),
    secondaryContainer = DeepChestnut,
    onSecondaryContainer = CalmAmber,
    tertiary = AmberOrange,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    outlineVariant = SoftBronze
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NafasDarkColorScheme,
        typography = Typography,
        content = content
    )
}
