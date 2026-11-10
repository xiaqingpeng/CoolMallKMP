package com.joker.coolmall.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.ui.theme.ColorDanger
import com.joker.coolmall.core.ui.theme.ColorPurple
import com.joker.coolmall.core.ui.theme.ColorSuccess
import com.joker.coolmall.core.ui.theme.ColorWarning
import com.joker.coolmall.core.ui.theme.GradientPrimaryEnd
import com.joker.coolmall.core.ui.theme.GradientPrimaryStart
import com.joker.coolmall.core.ui.theme.PrimaryDefault
import com.joker.coolmall.core.ui.theme.TextWhite

/**
 * 按钮类型（对齐 CoolMallKotlin ButtonType）
 */
enum class AppButtonType {
    DEFAULT,
    SUCCESS,
    WARNING,
    DANGER,
    PURPLE,
}

/**
 * 按钮样式
 */
enum class AppButtonStyle {
    FILLED,
    OUTLINED,
    GRADIENT,
}

/**
 * 按钮尺寸
 */
enum class AppButtonSize {
    /** 48dp */
    MEDIUM,

    /** 40dp */
    SMALL,

    /** 34dp */
    MINI,
}

/**
 * 按钮形状
 */
enum class AppButtonShape {
    SQUARE,
    ROUND,
}

/**
 * 主按钮（对齐 CoolMallKotlin AppButton）
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: AppButtonType = AppButtonType.DEFAULT,
    style: AppButtonStyle = AppButtonStyle.FILLED,
    size: AppButtonSize = AppButtonSize.MEDIUM,
    shape: AppButtonShape = AppButtonShape.ROUND,
    enabled: Boolean = true,
    loading: Boolean = false,
    fullWidth: Boolean = true,
) {
    val buttonHeight = size.toHeight()
    val buttonModifier = if (fullWidth) {
        modifier.fillMaxWidth().height(buttonHeight)
    } else {
        modifier.height(buttonHeight)
    }
    AppButtonContent(
        text = text,
        onClick = onClick,
        modifier = buttonModifier,
        buttonColor = type.toColor(),
        buttonShape = shape.toShape(buttonHeight),
        style = style,
        enabled = enabled,
        loading = loading,
        contentPadding = PaddingValues(horizontal = 16.dp),
    )
}

/**
 * 固定高度、自适应宽度（对齐 CoolMallKotlin AppButtonFixed）
 */
@Composable
fun AppButtonFixed(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: AppButtonType = AppButtonType.DEFAULT,
    style: AppButtonStyle = AppButtonStyle.FILLED,
    size: AppButtonSize = AppButtonSize.SMALL,
    shape: AppButtonShape = AppButtonShape.ROUND,
    enabled: Boolean = true,
    loading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
) {
    val buttonHeight = size.toHeight()
    AppButtonContent(
        text = text,
        onClick = onClick,
        modifier = modifier.height(buttonHeight),
        buttonColor = type.toColor(),
        buttonShape = shape.toShape(buttonHeight),
        style = style,
        enabled = enabled,
        loading = loading,
        contentPadding = contentPadding,
    )
}

/**
 * 描边按钮（对齐 CoolMallKotlin AppButtonBordered）
 */
@Composable
fun AppButtonBordered(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: AppButtonType = AppButtonType.DEFAULT,
    size: AppButtonSize = AppButtonSize.SMALL,
    shape: AppButtonShape = AppButtonShape.ROUND,
    enabled: Boolean = true,
    loading: Boolean = false,
    fullWidth: Boolean = false,
) {
    AppButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        type = type,
        style = AppButtonStyle.OUTLINED,
        size = size,
        shape = shape,
        enabled = enabled,
        loading = loading,
        fullWidth = fullWidth,
    )
}

@Composable
private fun AppButtonContent(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    buttonColor: Color,
    buttonShape: RoundedCornerShape,
    style: AppButtonStyle,
    enabled: Boolean,
    loading: Boolean,
    contentPadding: PaddingValues,
) {
    val clickable = enabled && !loading
    when (style) {
        AppButtonStyle.FILLED -> {
            Button(
                onClick = onClick,
                enabled = clickable,
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = buttonColor.copy(alpha = 0.5f),
                    contentColor = TextWhite,
                    disabledContentColor = TextWhite.copy(alpha = 0.8f),
                ),
                contentPadding = contentPadding,
                modifier = modifier,
            ) {
                ButtonLabel(text = text, loading = loading, color = TextWhite)
            }
        }

        AppButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                enabled = clickable,
                shape = buttonShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = buttonColor,
                    disabledContentColor = buttonColor.copy(alpha = 0.5f),
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (clickable) buttonColor else buttonColor.copy(alpha = 0.5f),
                ),
                contentPadding = contentPadding,
                modifier = modifier,
            ) {
                ButtonLabel(text = text, loading = loading, color = buttonColor)
            }
        }

        AppButtonStyle.GRADIENT -> {
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(GradientPrimaryStart, GradientPrimaryEnd),
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .clip(buttonShape)
                    .background(
                        brush = gradientBrush,
                        alpha = if (clickable) 1f else 0.5f,
                    )
                    .clickable(enabled = clickable, onClick = onClick)
                    .padding(contentPadding),
            ) {
                ButtonLabel(text = text, loading = loading, color = TextWhite)
            }
        }
    }
}

@Composable
private fun ButtonLabel(
    text: String,
    loading: Boolean,
    color: Color,
) {
    if (loading) {
        CircularProgressIndicator(
            color = color,
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
        )
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}

private fun AppButtonType.toColor(): Color = when (this) {
    AppButtonType.DEFAULT -> PrimaryDefault
    AppButtonType.SUCCESS -> ColorSuccess
    AppButtonType.WARNING -> ColorWarning
    AppButtonType.DANGER -> ColorDanger
    AppButtonType.PURPLE -> ColorPurple
}

private fun AppButtonSize.toHeight(): Dp = when (this) {
    AppButtonSize.MEDIUM -> 48.dp
    AppButtonSize.SMALL -> 40.dp
    AppButtonSize.MINI -> 34.dp
}

private fun AppButtonShape.toShape(height: Dp): RoundedCornerShape = when (this) {
    AppButtonShape.SQUARE -> RoundedCornerShape(8.dp)
    AppButtonShape.ROUND -> RoundedCornerShape(height / 2)
}
