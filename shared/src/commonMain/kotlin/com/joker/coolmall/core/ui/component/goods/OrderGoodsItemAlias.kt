package com.joker.coolmallkmp.core.ui.component.goods

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joker.coolmallkmp.core.model.entity.CartGoodsSpec
import com.joker.coolmallkmp.core.ui.component.OrderGoodsItem as SharedOrderGoodsItem

@Composable
fun OrderGoodsItem(
    data: CartGoodsSpec,
    goodsId: Long = data.goodsId,
    goodsName: String = "",
    goodsMainPic: String = "",
    modifier: Modifier = Modifier,
    onSpecClick: (Long) -> Unit = {},
    onQuantityChanged: (Int) -> Unit = {},
    enableQuantityStepper: Boolean = true,
    selectSlot: (@Composable () -> Unit)? = null,
    actionSlot: (@Composable () -> Unit)? = null,
) = SharedOrderGoodsItem(
    data = data,
    goodsName = goodsName,
    goodsMainPic = goodsMainPic,
    modifier = modifier,
    onSpecClick = onSpecClick,
    onQuantityChanged = onQuantityChanged,
    enableQuantityStepper = enableQuantityStepper,
    leadingContent = selectSlot,
    trailingContent = actionSlot,
)
