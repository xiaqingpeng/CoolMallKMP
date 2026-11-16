package com.joker.coolmallkmp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DisplayLarge = TextStyle(fontSize = 22.sp, lineHeight = 31.sp, fontWeight = FontWeight.SemiBold)
val DisplayMedium = TextStyle(fontSize = 18.sp, lineHeight = 27.sp, fontWeight = FontWeight.SemiBold)
val TitleLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold)
val TitleMedium = TextStyle(fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
val BodyLarge = TextStyle(fontSize = 14.sp, lineHeight = 22.sp)
val BodyMedium = TextStyle(fontSize = 12.sp, lineHeight = 18.sp)

val AppTypography = Typography(
    displayLarge = DisplayLarge,
    displayMedium = DisplayMedium,
    titleLarge = TitleLarge,
    titleMedium = TitleMedium,
    bodyLarge = BodyLarge,
    bodyMedium = BodyMedium,
    bodySmall = BodyMedium.copy(fontSize = 11.sp, lineHeight = 16.sp),
    labelSmall = BodyMedium.copy(fontSize = 11.sp, lineHeight = 16.sp),
)
