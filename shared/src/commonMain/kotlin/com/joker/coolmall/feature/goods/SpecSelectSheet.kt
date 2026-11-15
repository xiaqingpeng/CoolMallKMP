package com.joker.coolmall.feature.goods

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.joker.coolmall.core.model.entity.Goods
import com.joker.coolmall.core.model.entity.GoodsSpec
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonBordered
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.core.ui.component.PriceTextLarge
import com.joker.coolmall.core.ui.component.TitleWithLine
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.theme.ColorDanger
import com.joker.coolmall.core.ui.theme.SpaceHorizontalMedium
import com.joker.coolmall.core.ui.theme.SpaceHorizontalSmall
import com.joker.coolmall.core.ui.theme.SpacePaddingLarge
import com.joker.coolmall.core.ui.theme.SpacePaddingSmall
import com.joker.coolmall.core.ui.theme.SpaceVerticalLarge
import com.joker.coolmall.core.ui.theme.SpaceVerticalMedium
import com.joker.coolmall.core.ui.theme.SpaceVerticalSmall
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.goods_quantity
import myapplication.shared.generated.resources.modal_add_to_cart
import myapplication.shared.generated.resources.modal_buy_now
import myapplication.shared.generated.resources.modal_close
import myapplication.shared.generated.resources.modal_select_spec
import myapplication.shared.generated.resources.quantity_decrease
import myapplication.shared.generated.resources.quantity_increase
import myapplication.shared.generated.resources.spec_category
import myapplication.shared.generated.resources.spec_empty_fallback
import myapplication.shared.generated.resources.spec_not_selected
import myapplication.shared.generated.resources.spec_out_of_stock
import myapplication.shared.generated.resources.spec_selected
import myapplication.shared.generated.resources.spec_stock
import myapplication.shared.generated.resources.spec_toggle_grid
import myapplication.shared.generated.resources.spec_toggle_list
import org.jetbrains.compose.resources.stringResource

/**
 * 规格选择 BottomSheet（对齐 CoolMallKotlin SpecSelectModal 主结构）
 *
 * - 100dp 大图 header + 价格/已选/库存
 * - 网格规格卡片（图 + 名）
 * - 仅选中规格后显示数量
 * - 双 CTA：加购 + 立即购买
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SpecSelectSheet(
    goods: Goods,
    specsUiState: SpecsUiState,
    selectedSpec: GoodsSpec?,
    quantity: Int,
    isAdding: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onSpecSelected: (GoodsSpec) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shapeSmall = RoundedCornerShape(8.dp)
    val primary = MaterialTheme.colorScheme.primary
    var isGridMode by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacePaddingLarge)
                .navigationBarsPadding()
                .padding(bottom = SpaceVerticalLarge),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.modal_select_spec),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(Res.string.modal_close),
                    )
                }
            }

            SpecHeader(goods = goods, selectedSpec = selectedSpec, shape = shapeSmall)
            Spacer(modifier = Modifier.height(SpaceVerticalMedium))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(SpaceVerticalMedium))

            when (val state = specsUiState) {
                SpecsUiState.Idle, SpecsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SpecsUiState.Error -> {
                    EmptyUiMessage(
                        message = state.message,
                        onRetryClick = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    )
                }

                is SpecsUiState.Success -> {
                    if (state.specs.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.spec_empty_fallback),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(SpaceVerticalLarge))
                        SpecBottomButtons(
                            enabled = !isAdding,
                            isAdding = isAdding,
                            onAddToCart = onAddToCart,
                            onBuyNow = onBuyNow,
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TitleWithLine(
                                text = stringResource(Res.string.spec_category),
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { isGridMode = !isGridMode }) {
                                Icon(
                                    imageVector = if (isGridMode) {
                                        Icons.Filled.Menu
                                    } else {
                                        Icons.Filled.GridView
                                    },
                                    contentDescription = if (isGridMode) {
                                        stringResource(Res.string.spec_toggle_list)
                                    } else {
                                        stringResource(Res.string.spec_toggle_grid)
                                    },
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(SpaceVerticalMedium))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            AnimatedContent(
                                targetState = isGridMode,
                                transitionSpec = {
                                    (fadeIn(animationSpec = tween(300, easing = LinearEasing)) +
                                        scaleIn(
                                            initialScale = 0.92f,
                                            animationSpec = tween(300, easing = LinearEasing),
                                        )) togetherWith
                                        (fadeOut(animationSpec = tween(300, easing = LinearEasing)) +
                                            scaleOut(
                                                targetScale = 0.92f,
                                                animationSpec = tween(300, easing = LinearEasing),
                                            ))
                                },
                                label = "spec_view_mode",
                            ) { gridMode ->
                                if (gridMode) {
                                    GridSpecSelection(
                                        specs = state.specs,
                                        selectedId = selectedSpec?.id,
                                        coverImage = goods.mainPic,
                                        primary = primary,
                                        shape = shapeSmall,
                                        onSpecSelected = onSpecSelected,
                                    )
                                } else {
                                    ListSpecSelection(
                                        specs = state.specs,
                                        selectedId = selectedSpec?.id,
                                        coverImage = goods.mainPic,
                                        primary = primary,
                                        shape = shapeSmall,
                                        onSpecSelected = onSpecSelected,
                                    )
                                }
                            }
                        }

                        if (selectedSpec != null) {
                            Spacer(modifier = Modifier.height(SpaceVerticalLarge))
                            QuantitySelector(
                                quantity = quantity,
                                enabled = !isAdding,
                                onIncrease = onIncrease,
                                onDecrease = onDecrease,
                            )
                        }

                        Spacer(modifier = Modifier.height(SpaceVerticalLarge))
                        val hasStock = (selectedSpec?.stock ?: 0) > 0
                        SpecBottomButtons(
                            enabled = !isAdding && selectedSpec != null && hasStock,
                            isAdding = isAdding,
                            onAddToCart = onAddToCart,
                            onBuyNow = onBuyNow,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecHeader(
    goods: Goods,
    selectedSpec: GoodsSpec?,
    shape: RoundedCornerShape,
) {
    val image = selectedSpec?.images?.firstOrNull()?.takeIf { it.isNotBlank() }
        ?: goods.mainPic
    val price = selectedSpec?.price?.takeIf { it > 0 } ?: goods.price

    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = image,
            contentDescription = goods.title,
            modifier = Modifier
                .size(100.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(SpaceHorizontalMedium))
        Column(modifier = Modifier.weight(1f)) {
            PriceTextLarge(price = price)
            Spacer(modifier = Modifier.height(SpaceVerticalSmall))
            if (selectedSpec != null) {
                Text(
                    text = stringResource(Res.string.spec_selected, selectedSpec.name),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Text(
                    text = stringResource(Res.string.spec_not_selected),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(SpaceVerticalSmall))
            Text(
                text = stringResource(Res.string.spec_stock, selectedSpec?.stock ?: 0),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GridSpecSelection(
    specs: List<GoodsSpec>,
    selectedId: Long?,
    coverImage: String,
    primary: Color,
    shape: RoundedCornerShape,
    onSpecSelected: (GoodsSpec) -> Unit,
) {
    val itemsPerRow = 3
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpaceVerticalSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceVerticalSmall),
        maxItemsInEachRow = itemsPerRow,
    ) {
        specs.forEach { spec ->
            val selected = selectedId == spec.id
            val outOfStock = spec.stock <= 0
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 130.dp)
                    .clip(shape)
                    .border(
                        width = 1.dp,
                        color = if (selected) {
                            primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                        },
                        shape = shape,
                    )
                    .background(
                        if (selected) primary.copy(alpha = 0.05f) else Color.Transparent,
                    )
                    .clickable(enabled = !outOfStock) { onSpecSelected(spec) },
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val specImage = spec.images?.firstOrNull()?.takeIf { it.isNotBlank() }
                        ?: coverImage
                    AsyncImage(
                        model = specImage,
                        contentDescription = spec.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 8.dp,
                                    topEnd = 8.dp,
                                ),
                            )
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop,
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .padding(horizontal = SpaceHorizontalSmall),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = if (outOfStock) {
                                "${spec.name}(${stringResource(Res.string.spec_out_of_stock)})"
                            } else {
                                spec.name
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                outOfStock -> MaterialTheme.colorScheme.onSurfaceVariant
                                selected -> primary
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        val remaining = (itemsPerRow - (specs.size % itemsPerRow)) % itemsPerRow
        repeat(remaining) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 130.dp),
            )
        }
    }
}

@Composable
private fun ListSpecSelection(
    specs: List<GoodsSpec>,
    selectedId: Long?,
    coverImage: String,
    primary: Color,
    shape: RoundedCornerShape,
    onSpecSelected: (GoodsSpec) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium)) {
        specs.forEach { spec ->
            val selected = selectedId == spec.id
            val outOfStock = spec.stock <= 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .border(
                        width = 1.dp,
                        color = if (selected) {
                            primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                        },
                        shape = shape,
                    )
                    .background(
                        if (selected) primary.copy(alpha = 0.05f) else Color.Transparent,
                    )
                    .clickable(enabled = !outOfStock) { onSpecSelected(spec) }
                    .padding(SpacePaddingSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val specImage = spec.images?.firstOrNull()?.takeIf { it.isNotBlank() } ?: coverImage
                AsyncImage(
                    model = specImage,
                    contentDescription = spec.name,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(SpaceHorizontalSmall))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (outOfStock) {
                            "${spec.name}(${stringResource(Res.string.spec_out_of_stock)})"
                        } else {
                            spec.name
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            outOfStock -> MaterialTheme.colorScheme.onSurfaceVariant
                            selected -> primary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¥${spec.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorDanger,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    enabled: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TitleWithLine(
            text = stringResource(Res.string.goods_quantity),
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onDecrease, enabled = enabled && quantity > 1) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = stringResource(Res.string.quantity_decrease),
            )
        }
        Text(
            text = "$quantity",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = SpacePaddingSmall),
        )
        IconButton(onClick = onIncrease, enabled = enabled) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(Res.string.quantity_increase),
            )
        }
    }
}

@Composable
private fun SpecBottomButtons(
    enabled: Boolean,
    isAdding: Boolean,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpaceHorizontalMedium),
    ) {
        AppButtonBordered(
            text = stringResource(Res.string.modal_add_to_cart),
            onClick = onAddToCart,
            modifier = Modifier.weight(1f),
            size = AppButtonSize.SMALL,
            enabled = enabled,
            loading = isAdding,
            fullWidth = true,
        )
        AppButton(
            text = stringResource(Res.string.modal_buy_now),
            onClick = onBuyNow,
            modifier = Modifier.weight(1f),
            size = AppButtonSize.SMALL,
            style = AppButtonStyle.GRADIENT,
            enabled = enabled,
            fullWidth = true,
        )
    }
}
