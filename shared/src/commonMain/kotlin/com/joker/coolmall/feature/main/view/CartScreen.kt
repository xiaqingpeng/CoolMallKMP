package com.joker.coolmall.feature.main.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.model.entity.Cart
import com.joker.coolmall.core.model.entity.CartGoodsSpec
import com.joker.coolmall.core.order.CheckoutDraft
import com.joker.coolmall.core.ui.component.AppButtonFixed
import com.joker.coolmall.core.ui.component.AppCard
import com.joker.coolmall.core.ui.component.RefreshLayout
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import com.joker.coolmall.core.ui.component.PriceText
import com.joker.coolmall.core.ui.component.PriceTextSmall
import com.joker.coolmall.core.ui.empty.EmptyCart
import com.joker.coolmall.feature.main.CartViewModel
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.feature.main.component.CommonScaffold
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.cart_checkout
import myapplication.shared.generated.resources.cart_decrease
import myapplication.shared.generated.resources.cart_default_spec
import myapplication.shared.generated.resources.cart_increase
import myapplication.shared.generated.resources.cart_title
import myapplication.shared.generated.resources.cart_total_prefix
import myapplication.shared.generated.resources.cart_select_all
import myapplication.shared.generated.resources.cart_selected_count
import myapplication.shared.generated.resources.cart_delete_selected
import myapplication.shared.generated.resources.common_complete
import myapplication.shared.generated.resources.common_delete
import myapplication.shared.generated.resources.common_edit
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CartRoute(
    onCheckoutClick: () -> Unit = {},
    onLoginRequired: () -> Unit = {},
    onGoodsClick: (Long) -> Unit = {},
    viewModel: CartViewModel = viewModel(
        factory = viewModelFactory {
            initializer { CartViewModel(AppContainerProvider.instance.cartRepository) }
        },
    ),
) {
    val carts by viewModel.cartItems.collectAsState()
    val isLoggedIn by AppContainerProvider.instance.authRepository.isLoggedIn.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val selectedItems by viewModel.selectedItems.collectAsState()
    val isAllSelected by viewModel.isAllSelected.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()
    val selectedAmount by viewModel.selectedAmount.collectAsState()
    var deletingItems by remember { mutableStateOf(emptySet<String>()) }
    val deleteScope = rememberCoroutineScope()

    CommonScaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.cart_title),
                actions = {
                    if (carts.isNotEmpty()) {
                        TextButton(onClick = viewModel::toggleEditMode) {
                            Text(
                                stringResource(
                                    if (isEditing) Res.string.common_complete else Res.string.common_edit,
                                ),
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (carts.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(Res.string.cart_selected_count, selectedCount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (!isEditing) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    stringResource(Res.string.cart_total_prefix),
                                    fontWeight = FontWeight.Bold,
                                )
                                PriceText(price = selectedAmount, showDecimals = false)
                            }
                        }
                    }
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = { viewModel.toggleSelectAll() },
                    )
                    Text(stringResource(Res.string.cart_select_all))
                    if (isEditing) {
                        AppButtonFixed(
                            text = stringResource(Res.string.cart_delete_selected),
                            onClick = viewModel::deleteSelected,
                            enabled = selectedCount > 0,
                            size = AppButtonSize.SMALL,
                            style = AppButtonStyle.GRADIENT,
                        )
                    } else {
                        AppButtonFixed(
                            text = stringResource(Res.string.cart_checkout),
                            onClick = {
                                if (!isLoggedIn) {
                                    onLoginRequired()
                                } else {
                                    val selected = viewModel.selectedCarts()
                                    if (selected.isNotEmpty()) {
                                        CheckoutDraft.prepareFromCart(selected)
                                        onCheckoutClick()
                                    }
                                }
                            },
                            enabled = selectedCount > 0,
                            size = AppButtonSize.SMALL,
                            style = AppButtonStyle.GRADIENT,
                        )
                    }
                    }
                }
            }
        },
    ) { padding ->
        if (carts.isEmpty()) {
            EmptyCart(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            val flatItems = remember(carts) {
                carts.flatMap { cart -> cart.spec.map { cart to it } }
            }
            RefreshLayout(
                modifier = Modifier.fillMaxSize().padding(padding),
                onRefresh = {},
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(flatItems, key = { (cart, spec) -> "${cart.goodsId}_${spec.id}" }) { (cart, spec) ->
                    val itemKey = "${cart.goodsId}_${spec.id}"
                    AnimatedVisibility(
                        visible = itemKey !in deletingItems,
                        exit = slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300),
                        ) + fadeOut(animationSpec = tween(300)),
                    ) {
                        CartItemCard(
                            cart = cart,
                            spec = spec,
                            onIncrease = { viewModel.increase(cart.goodsId, spec.id, spec.count) },
                            onDecrease = { viewModel.decrease(cart.goodsId, spec.id, spec.count) },
                            onRemove = {
                                if (itemKey !in deletingItems) {
                                    deletingItems = deletingItems + itemKey
                                    deleteScope.launch {
                                        delay(300)
                                        viewModel.remove(cart.goodsId, spec.id)
                                        deletingItems = deletingItems - itemKey
                                    }
                                }
                            },
                            selected = selectedItems[cart.goodsId]?.contains(spec.id) == true,
                            onSelectionChanged = { viewModel.toggleSelection(cart.goodsId, spec.id) },
                            onGoodsClick = { onGoodsClick(cart.goodsId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cart: Cart,
    spec: CartGoodsSpec,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    selected: Boolean,
    onSelectionChanged: () -> Unit,
    onGoodsClick: () -> Unit,
) {
    val image = spec.images?.firstOrNull()?.takeIf(String::isNotBlank) ?: cart.goodsMainPic
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onGoodsClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onSelectionChanged() },
            )
            AsyncImage(
                model = image,
                contentDescription = cart.goodsName,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cart.goodsName,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    spec.name.ifBlank { stringResource(Res.string.cart_default_spec) },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                PriceTextSmall(price = spec.price)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, stringResource(Res.string.cart_decrease))
                    }
                    Text("${spec.count}", modifier = Modifier.padding(horizontal = 6.dp))
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, stringResource(Res.string.cart_increase))
                    }
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.common_delete),
                )
            }
        }
    }
}
