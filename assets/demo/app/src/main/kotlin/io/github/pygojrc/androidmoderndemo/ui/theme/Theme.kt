package io.github.pygojrc.androidmoderndemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = RosePrimary,
    onPrimary = RoseOnPrimary,
    primaryContainer = RosePrimaryContainer,
    onPrimaryContainer = RoseOnPrimaryContainer,
    secondary = TealSecondary,
    onSecondary = TealOnSecondary,
    background = LightBackground,
    surface = LightSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkRosePrimary,
    onPrimary = DarkRoseOnPrimary,
    primaryContainer = DarkRosePrimaryContainer,
    onPrimaryContainer = DarkRoseOnPrimaryContainer,
    secondary = DarkTealSecondary,
    onSecondary = DarkTealOnSecondary,
    background = DarkBackground,
    surface = DarkSurface,
)

@Composable
fun AndroidModernDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor -> dynamicLightColorScheme(context)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
