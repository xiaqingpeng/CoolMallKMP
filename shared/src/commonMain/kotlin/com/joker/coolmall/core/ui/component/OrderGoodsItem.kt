package com.joker.coolmall.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.joker.coolmall.core.model.entity.CartGoodsSpec
import com.joker.coolmall.core.model.entity.OrderGoods

@Composable
fun OrderGoodsItem(
    data: CartGoodsSpec,
    goodsName: String = "",
    goodsMainPic: String = "",
    modifier: Modifier = Modifier,
    enableQuantityStepper: Boolean = true,
    onSpecClick: (Long) -> Unit = {},
    onQuantityChanged: (Int) -> Unit = {},
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingContent?.invoke()
        AsyncImage(
            model = data.images?.firstOrNull() ?: goodsMainPic,
            contentDescription = goodsName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(88.dp).clip(RoundedCornerShape(8.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(goodsName, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            Text(
                text = data.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    .clickable { onSpecClick(data.id) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                PriceText(data.price)
                Spacer(Modifier.weight(1f))
                trailingContent?.invoke()
                    ?: Text(
                        if (enableQuantityStepper) "x${data.count}" else "数量 ${data.count}",
                        style = MaterialTheme.typography.bodySmall,
                    )
            }
        }
    }
}

@Composable
fun OrderGoodsItem(
    data: OrderGoods,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = data.displayPic,
            contentDescription = data.goodsName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(88.dp).clip(RoundedCornerShape(8.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(data.goodsName, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            data.specName.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                PriceText(data.price.toInt())
                Spacer(Modifier.weight(1f))
                Text("x${data.count}", style = MaterialTheme.typography.bodySmall)
                trailingContent?.invoke()
            }
        }
    }
}
