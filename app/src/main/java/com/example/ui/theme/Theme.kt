package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BizzyPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = BizzyPrimary,
    onPrimaryContainer = Color.White,
    secondary = BizzyAccent,
    onSecondary = Color.White,
    secondaryContainer = BizzyAccentDark,
    onSecondaryContainer = Color.White,
    background = BizzyPrimaryDark,
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    outline = Color(0xFF475569)
)

private val LightColorScheme = lightColorScheme(
    primary = BizzyPrimary,
    onPrimary = Color.White,
    primaryContainer = BizzyPrimaryContainer,
    onPrimaryContainer = BizzyPrimary,
    secondary = BizzyAccent,
    onSecondary = Color.White,
    secondaryContainer = BizzyAccentLight,
    onSecondaryContainer = BizzyAccentDark,
    tertiary = BizzyGreen,
    onTertiary = Color.White,
    background = BizzyBackground,
    surface = BizzySurface,
    surfaceVariant = BizzySurfaceVariant,
    onBackground = BizzyTextPrimary,
    onSurface = BizzyTextPrimary,
    outline = BizzyBorder
)

@Composable
fun BizzyCartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep BizzyCart distinctive brand identity by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
