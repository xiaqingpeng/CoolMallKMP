package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joker.coolmallkmp.core.ui.theme.ColorDanger

/**
 * 价格文本（对齐 CoolMallKotlin PriceText）
 *
 * 结构：¥ + 整数 + .00，基线对齐；默认危险红。
 *
 * @param price 价格（分/元整数，与业务模型一致）
 * @param showDecimals 是否显示 `.00` 小数尾巴
 * @param color 价格颜色，默认 [ColorDanger]
 * @param integerStyle 整数部分样式
 * @param symbolStyle 货币符号样式
 * @param decimalStyle 小数部分样式
 */
@Composable
fun PriceText(
    price: Int,
    modifier: Modifier = Modifier,
    showDecimals: Boolean = true,
    color: Color = ColorDanger,
    integerStyle: TextStyle = MaterialTheme.typography.titleMedium,
    symbolStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    decimalStyle: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = "¥",
            style = symbolStyle,
            color = color,
            modifier = Modifier
                .alignByBaseline()
                .padding(end = 1.dp),
        )
        Text(
            text = price.toString(),
            style = integerStyle,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alignByBaseline(),
        )
        if (showDecimals) {
            Text(
                text = ".00",
                style = decimalStyle,
                color = color,
                modifier = Modifier.alignByBaseline(),
            )
        }
    }
}

/** 商详/支付等大号价格 */
@Composable
fun PriceTextLarge(
    price: Int,
    modifier: Modifier = Modifier,
    showDecimals: Boolean = true,
) {
    PriceText(
        price = price,
        modifier = modifier,
        showDecimals = showDecimals,
        integerStyle = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp),
        symbolStyle = MaterialTheme.typography.titleMedium,
        decimalStyle = MaterialTheme.typography.bodyMedium,
    )
}

/** 列表卡片小号价格 */
@Composable
fun PriceTextSmall(
    price: Int,
    modifier: Modifier = Modifier,
    showDecimals: Boolean = false,
) {
    PriceText(
        price = price,
        modifier = modifier,
        showDecimals = showDecimals,
        integerStyle = MaterialTheme.typography.titleSmall,
        symbolStyle = MaterialTheme.typography.labelMedium,
        decimalStyle = MaterialTheme.typography.labelSmall,
    )
}
