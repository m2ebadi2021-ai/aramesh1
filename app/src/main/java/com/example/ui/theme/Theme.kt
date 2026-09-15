package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeSetting(val id: String, val titleFa: String, val titleEn: String) {
    CALM("calm", "آرامش (فیروزه‌ای)", "Calm Teal"),
    LAVENDER("lavender", "ارغوان و اسطوخودوس (شب آرام)", "Twilight Lavender"),
    DARK("dark", "شب تاریک (دارک)", "Dark Sanctuary"),
    WARM("warm", "پرتو گرم (خورشیدی)", "Warm Amber"),
    EMERALD("emerald", "زمرد (جنگل کهن)", "Emerald Forest")
}

private val CalmLightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondary = TealSecondary,
    onSecondary = TealOnSecondary,
    secondaryContainer = TealSecondaryContainer,
    onSecondaryContainer = TealOnSecondaryContainer,
    tertiary = TealTertiary,
    onTertiary = TealOnTertiary,
    tertiaryContainer = TealTertiaryContainer,
    onTertiaryContainer = TealOnTertiaryContainer,
    background = TealBackground,
    onBackground = TealOnBackground,
    surface = TealSurface,
    onSurface = TealOnSurface,
    surfaceVariant = TealSurfaceVariant,
    onSurfaceVariant = TealOnSurfaceVariant
)

private val CalmDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    tertiary = GoldenFruitColor,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val WarmLightColorScheme = lightColorScheme(
    primary = WarmPrimary,
    secondary = WarmSecondary,
    background = WarmBackground,
    surface = WarmSurface,
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF7C2D12)
)

private val EmeraldLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    secondary = EmeraldSecondary,
    background = EmeraldBackground,
    surface = EmeraldSurface,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B)
)

private val LavenderLightColorScheme = lightColorScheme(
    primary = LavenderPrimary,
    secondary = LavenderSecondary,
    background = LavenderBackground,
    surface = LavenderSurface,
    primaryContainer = LavenderPrimaryContainer,
    onPrimaryContainer = LavenderOnPrimaryContainer
)

@Composable
fun ArameshTheme(
    themeSetting: AppThemeSetting = AppThemeSetting.CALM,
    systemDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeSetting) {
        AppThemeSetting.CALM -> if (systemDark) CalmDarkColorScheme else CalmLightColorScheme
        AppThemeSetting.LAVENDER -> LavenderLightColorScheme
        AppThemeSetting.DARK -> CalmDarkColorScheme
        AppThemeSetting.WARM -> WarmLightColorScheme
        AppThemeSetting.EMERALD -> EmeraldLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
