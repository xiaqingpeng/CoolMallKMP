package com.joker.coolmallkmp.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RadiusXSmall = 4.dp
val RadiusSmall = 8.dp
val RadiusMedium = 12.dp
val RadiusLarge = 16.dp
val RadiusExtraLarge = 24.dp

val ShapeXSmall = RoundedCornerShape(RadiusXSmall)
val ShapeSmall = RoundedCornerShape(RadiusSmall)
val ShapeMedium = RoundedCornerShape(RadiusMedium)
val ShapeLarge = RoundedCornerShape(RadiusLarge)
val ShapeExtraLarge = RoundedCornerShape(RadiusExtraLarge)
val ShapeCircle = RoundedCornerShape(percent = 50)

val AppShapes = Shapes(
    small = ShapeSmall,
    medium = ShapeMedium,
    large = ShapeExtraLarge,
    extraLarge = ShapeExtraLarge,
)
