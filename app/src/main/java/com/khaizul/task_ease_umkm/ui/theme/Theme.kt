package com.khaizul.task_ease_umkm.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryCyan,
    tertiary = LightCyan,
    background = PureWhite,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = Color.Black,
    onBackground = Color(0xFF222222),
    onSurface = Color(0xFF222222)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    secondary = DarkSecondaryCyan,
    tertiary = DarkLightCyan,
    background = DarkBackground,
    surface = Color(0xFF1E1E1E),
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite
)

@Composable
fun TaskEaseUMKMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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