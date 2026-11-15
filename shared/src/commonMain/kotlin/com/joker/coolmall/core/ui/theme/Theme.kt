package com.joker.coolmall.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * CoolMall 品牌 ColorScheme（对齐 dump AppTheme 主色/危险色/背景）
 */
fun coolMallLightColorScheme(primary: Color = PrimaryDefault): ColorScheme =
    lightColorScheme(
        primary = primary,
        secondary = ColorPurple,
        tertiary = ColorSuccess,
        background = BgGreyLight,
        surface = BgWhiteLight,
        error = ColorDanger,
        onPrimary = TextWhite,
        onSecondary = TextWhite,
        onTertiary = TextWhite,
        onBackground = TextPrimaryLight,
        onSurface = TextPrimaryLight,
        onError = TextWhite,
        outline = BorderLight,
        surfaceVariant = BgContentLight,
        surfaceBright = BgWhiteLight,
        surfaceDim = BgGreyLight,
        surfaceContainerLowest = BgWhiteLight,
        surfaceContainerLow = BgWhiteLight,
        surfaceContainer = BgWhiteLight,
        surfaceContainerHigh = BgWhiteLight,
        surfaceContainerHighest = BgWhiteLight,
        surfaceTint = primary,
    )

fun coolMallDarkColorScheme(primary: Color = PrimaryDefault): ColorScheme =
    darkColorScheme(
        primary = primary,
        secondary = ColorPurple,
        tertiary = ColorSuccess,
        background = BgGreyDark,
        surface = BgWhiteDark,
        error = ColorDanger,
        onPrimary = TextWhite,
        onSecondary = TextWhite,
        onTertiary = TextWhite,
        onBackground = TextPrimaryDark,
        onSurface = TextPrimaryDark,
        onError = TextWhite,
        outline = BorderDark,
        surfaceVariant = BgContentDark,
        surfaceBright = BgWhiteDark,
        surfaceDim = BgGreyDark,
        surfaceContainerLowest = BgWhiteDark,
        surfaceContainerLow = BgWhiteDark,
        surfaceContainer = BgWhiteDark,
        surfaceContainerHigh = BgWhiteDark,
        surfaceContainerHighest = BgWhiteDark,
        surfaceTint = primary,
    )
