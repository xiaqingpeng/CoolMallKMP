//package com.joker.coolmall.feature.main
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Remove
//import androidx.compose.material3.Card
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.lifecycle.viewmodel.initializer
//import androidx.lifecycle.viewmodel.viewModelFactory
//import coil3.compose.AsyncImage
//import com.joker.coolmall.core.di.AppContainerProvider
//import com.joker.coolmall.core.model.entity.Cart
//import com.joker.coolmall.core.model.entity.CartGoodsSpec
//import com.joker.coolmall.core.order.CheckoutDraft
//import com.joker.coolmall.core.ui.component.AppButtonFixed
//import com.joker.coolmall.core.ui.component.AppButtonSize
//import com.joker.coolmall.core.ui.component.AppButtonStyle
//import com.joker.coolmall.core.ui.component.PriceText
//import com.joker.coolmall.core.ui.component.PriceTextSmall
//import com.joker.coolmall.core.ui.empty.EmptyCart
//import com.joker.coolmall.feature.main.component.CommonScaffold
//import myapplication.shared.generated.resources.Res
//import myapplication.shared.generated.resources.cart_checkout
//import myapplication.shared.generated.resources.cart_clear
//import myapplication.shared.generated.resources.cart_decrease
//import myapplication.shared.generated.resources.cart_default_spec
//import myapplication.shared.generated.resources.cart_increase
//import myapplication.shared.generated.resources.cart_title
//import myapplication.shared.generated.resources.cart_total_count
//import myapplication.shared.generated.resources.cart_total_prefix
//import myapplication.shared.generated.resources.common_delete
//import org.jetbrains.compose.resources.stringResource
//
//@Composable
//fun CartRoute(
//    onCheckoutClick: () -> Unit = {},
//    onLoginRequired: () -> Unit = {},
//    viewModel: CartViewModel = viewModel(
//        factory = viewModelFactory {
//            initializer {
//                CartViewModel(AppContainerProvider.instance.cartRepository)
//            }
//        }
//    ),
//) {
//    val carts by viewModel.cartItems.collectAsState()
//    val isEmpty by viewModel.isEmpty.collectAsState()
//    val totalAmount by viewModel.totalAmount.collectAsState()
//    val totalCount by viewModel.totalCount.collectAsState()
//    val isLoggedIn by AppContainerProvider.instance.authRepository.isLoggedIn.collectAsState()
//
//    CartScreen(
//        carts = carts,
//        isEmpty = isEmpty,
//        totalAmount = totalAmount,
//        totalCount = totalCount,
//        onIncrease = viewModel::increase,
//        onDecrease = viewModel::decrease,
//        onRemove = viewModel::remove,
//        onClear = viewModel::clear,
//        onCheckout = {
//            if (!isLoggedIn) {
//                onLoginRequired()
//                return@CartScreen
//            }
//            CheckoutDraft.prepareFromCart(carts)
//            onCheckoutClick()
//        },
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun CartScreen(
//    carts: List<Cart>,
//    isEmpty: Boolean,
//    totalAmount: Int,
//    totalCount: Int,
//    onIncrease: (Long, Long, Int) -> Unit,
//    onDecrease: (Long, Long, Int) -> Unit,
//    onRemove: (Long, Long) -> Unit,
//    onClear: () -> Unit,
//    onCheckout: () -> Unit,
//) {
//    // MainRoute 已 exclude statusBars；本页用 CommonScaffold/TopAppBar 自行吃状态栏 inset
//    CommonScaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(stringResource(Res.string.cart_title)) },
//                actions = {
//                    if (!isEmpty) {
//                        TextButton(onClick = onClear) {
//                            Text(stringResource(Res.string.cart_clear))
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.background,
//                    scrolledContainerColor = MaterialTheme.colorScheme.background,
//                ),
//            )
//        },
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding),
//        ) {
//        if (isEmpty) {
//            EmptyCart(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth(),
//            )
//        } else {
//            val flatItems = remember(carts) {
//                carts.flatMap { cart -> cart.spec.map { spec -> cart to spec } }
//            }
//            LazyColumn(
//                modifier = Modifier.weight(1f),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//            ) {
//                items(
//                    items = flatItems,
//                    key = { (cart, spec) -> "${cart.goodsId}_${spec.id}" },
//                ) { (cart, spec) ->
//                    CartItemCard(
//                        cart = cart,
//                        spec = spec,
//                        onIncrease = { onIncrease(cart.goodsId, spec.id, spec.count) },
//                        onDecrease = { onDecrease(cart.goodsId, spec.id, spec.count) },
//                        onRemove = { onRemove(cart.goodsId, spec.id) },
//                    )
//                }
//            }
//            HorizontalDivider()
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    Text(
//                        text = stringResource(Res.string.cart_total_count, totalCount),
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    )
//                    Row(verticalAlignment = Alignment.Bottom) {
//                        Text(
//                            text = stringResource(Res.string.cart_total_prefix),
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold,
//                        )
//                        PriceText(
//                            price = totalAmount,
//                            showDecimals = false,
//                            integerStyle = MaterialTheme.typography.titleMedium,
//                        )
//                    }
//                }
//                AppButtonFixed(
//                    text = stringResource(Res.string.cart_checkout),
//                    onClick = onCheckout,
//                    size = AppButtonSize.SMALL,
//                    style = AppButtonStyle.GRADIENT,
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun CartItemCard(
//    cart: Cart,
//    spec: CartGoodsSpec,
//    onIncrease: () -> Unit,
//    onDecrease: () -> Unit,
//    onRemove: () -> Unit,
//) {
//    val image = spec.images?.firstOrNull()?.takeIf { it.isNotBlank() }
//        ?: cart.goodsMainPic
//
//    Card(modifier = Modifier.fillMaxWidth()) {
//        Row(
//            modifier = Modifier.padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            AsyncImage(
//                model = image,
//                contentDescription = cart.goodsName,
//                modifier = Modifier
//                    .size(72.dp)
//                    .clip(RoundedCornerShape(8.dp)),
//                contentScale = ContentScale.Crop,
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = cart.goodsName,
//                    style = MaterialTheme.typography.titleSmall,
//                    fontWeight = FontWeight.Medium,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis,
//                )
//                Text(
//                    text = spec.name.ifBlank { stringResource(Res.string.cart_default_spec) },
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                )
//                PriceTextSmall(price = spec.price)
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
//                        Icon(
//                            Icons.Filled.Remove,
//                            contentDescription = stringResource(Res.string.cart_decrease),
//                        )
//                    }
//                    Text(
//                        text = "${spec.count}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        modifier = Modifier.padding(horizontal = 4.dp),
//                    )
//                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
//                        Icon(
//                            Icons.Filled.Add,
//                            contentDescription = stringResource(Res.string.cart_increase),
//                        )
//                    }
//                }
//            }
//            IconButton(onClick = onRemove) {
//                Icon(
//                    imageVector = Icons.Filled.Delete,
//                    contentDescription = stringResource(Res.string.common_delete),
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                )
//            }
//        }
//    }
//}}

package com.joker.coolmall.feature.main.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.model.entity.Cart
import com.joker.coolmall.core.ui.component.AppButtonFixed
import com.joker.coolmall.core.ui.component.PriceText
import com.joker.coolmall.core.ui.empty.EmptyCart
import com.joker.coolmall.feature.main.CartViewModel
import com.joker.coolmall.feature.main.component.CommonScaffold
import org.jetbrains.compose.resources.stringResource

/**
 * 购物车页面路由
 *
 * @param navKey 路由参数
 * @param viewModel 购物车ViewModel
 * @author Joker.X
 */
@Composable
internal fun CartRoute(
    navKey: MainRoutes.Cart,
    viewModel: CartViewModel = hiltViewModel<CartViewModel, CartViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(navKey)
        }
    ),
) {
    // 购物车商品列表
    val carts by viewModel.cartItems.collectAsState()
    // 购物车是否为空
    val isEmpty by viewModel.isEmpty.collectAsState()
    // 是否处于编辑模式
    val isEditing by viewModel.isEditing.collectAsState()
    // 是否全选状态
    val isAllSelected by viewModel.isAllSelected.collectAsState()
    // 已选商品数量
    val selectedCount by viewModel.selectedCount.collectAsState()
    // 已选商品总金额
    val selectedTotalAmount by viewModel.selectedTotalAmount.collectAsState()
    // 是否显示返回按钮
    val showBackIcon by viewModel.showBackIcon.collectAsState()
    // 已选商品和规格ID的映射
    val selectedItems by viewModel.selectedItems.collectAsState()

    CartScreen(
        carts = carts,
        isEmpty = isEmpty,
        isEditing = isEditing,
        isAllSelected = isAllSelected,
        selectedCount = selectedCount,
        selectedTotalAmount = selectedTotalAmount,
        selectedItems = selectedItems,
        onToggleEditMode = viewModel::toggleEditMode,
        onToggleSelectAll = viewModel::toggleSelectAll,
        onToggleItemSelection = viewModel::toggleItemSelection,
        onUpdateCartItemCount = viewModel::updateCartItemCount,
        onDeleteSelected = viewModel::deleteSelectedItems,
        onSettleClick = viewModel::onCheckoutClick,
        showBackIcon = showBackIcon
    )
}

/**
 * 购物车页面内容
 *
 * @param carts 购物车商品列表
 * @param isEmpty 购物车是否为空
 * @param isEditing 是否处于编辑模式
 * @param isAllSelected 是否全选状态
 * @param selectedCount 已选商品数量
 * @param selectedTotalAmount 已选商品总金额
 * @param selectedItems 已选商品和规格ID的映射
 * @param onToggleEditMode 切换编辑模式回调
 * @param onToggleSelectAll 切换全选状态回调
 * @param onToggleItemSelection 切换商品选中状态回调
 * @param onUpdateCartItemCount 更新商品数量回调
 * @param onDeleteSelected 删除已选商品回调
 * @param onSettleClick 结算按钮点击回调
 * @param showBackIcon 是否显示返回按钮
 * @author Joker.X
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CartScreen(
    carts: List<Cart>? = null,
    isEmpty: Boolean = false,
    isEditing: Boolean = false,
    isAllSelected: Boolean = false,
    selectedCount: Int = 0,
    selectedTotalAmount: Int = 0,
    selectedItems: Map<Long, Set<Long>>? = null,
    onToggleEditMode: () -> Unit = {},
    onToggleSelectAll: () -> Unit = {},
    onToggleItemSelection: (Long, Long) -> Unit = { _, _ -> },
    onUpdateCartItemCount: (Long, Long, Int) -> Unit = { _, _, _ -> },
    onDeleteSelected: () -> Unit = {},
    onSettleClick: () -> Unit = {},
    showBackIcon: Boolean = false,
) {
    // 跟踪正在删除的商品
    var deletingItems by remember { mutableStateOf(emptyMap<Long, Set<Long>>()) }

    // 处理删除动画
    val handleDeleteWithAnimation = {
        deletingItems = selectedItems?.toMap() ?: emptyMap()
    }

    // 监听deletingItems变化，延迟后执行实际删除
    LaunchedEffect(deletingItems) {
        if (deletingItems.isNotEmpty()) {
            delay(300) // 等待动画完成
            onDeleteSelected()
            deletingItems = emptyMap()
        }
    }
    CommonScaffold(
        topBar = {
            CenterTopAppBar(
                title = R.string.cart,
                showBackIcon = showBackIcon,
                onBackClick = { navigateBack() },
                actions = {
                    if (!isEmpty) {
                        TextButton(onClick = onToggleEditMode) {
                            Text(
                                text = stringResource(id = if (isEditing) R.string.complete else R.string.edit),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            // 底部结算栏
            if (!isEmpty) {
                CartBottomBar(
                    isCheckAll = isAllSelected,
                    isEditing = isEditing,
                    selectedCount = selectedCount,
                    totalPrice = selectedTotalAmount,
                    onCheckAllChanged = onToggleSelectAll,
                    onDeleteClick = handleDeleteWithAnimation,
                    onSettleClick = onSettleClick,
                )
            }
        }
    ) { paddingValues ->
        // Screen层处理空状态
        if (isEmpty) {
            EmptyCart()
        } else {
            // 有数据时才渲染ContentView
            CartContentView(
                carts = carts ?: emptyList(),
                selectedItems = selectedItems ?: emptyMap(),
                deletingItems = deletingItems,
                onUpdateCartItemCount = onUpdateCartItemCount,
                onToggleItemSelection = onToggleItemSelection,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

/**
 * 购物车内容视图（仅展示成功状态）
 *
 * @param carts 购物车商品列表
 * @param selectedItems 已选商品和规格ID的映射
 * @param deletingItems 正在删除的商品和规格ID的映射
 * @param onUpdateCartItemCount 更新商品数量回调
 * @param onToggleItemSelection 切换商品选中状态回调
 * @param modifier 修饰符
 * @author Joker.X
 */
@Composable
private fun CartContentView(
    carts: List<Cart>,
    selectedItems: Map<Long, Set<Long>>,
    deletingItems: Map<Long, Set<Long>>,
    onUpdateCartItemCount: (Long, Long, Int) -> Unit,
    onToggleItemSelection: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 购物车商品列表
    LazyColumn(
        contentPadding = PaddingValues(SpacePaddingMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = carts,
            key = { "cart-${it.goodsId}" },
        ) { cart ->
            // 检查整个商品是否正在删除
            val cartSelectedSpecs = selectedItems[cart.goodsId] ?: emptySet()
            val cartDeletingSpecs = deletingItems[cart.goodsId] ?: emptySet()
            val isCartDeleting = cartSelectedSpecs.isNotEmpty() &&
                    cartSelectedSpecs == cart.spec.map { it.id }.toSet() &&
                    cartDeletingSpecs == cartSelectedSpecs

            AnimatedVisibility(
                visible = !isCartDeleting,
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                OrderGoodsCard(
                    data = cart,
                    deletingSpecIds = cartDeletingSpecs,
                    onGoodsClick = {
                        GoodsNavigator.toDetail(cart.goodsId)
                    },
                    onQuantityChanged = { specId, newCount ->
                        onUpdateCartItemCount(cart.goodsId, specId, newCount)
                    },
                    itemSelectSlot = { spec ->
                        // 每次渲染时都会重新计算选中状态
                        val selected =
                            selectedItems[cart.goodsId]?.contains(spec.id) == true
                        CheckButton(
                            selected = selected,
                            onClick = { onToggleItemSelection(cart.goodsId, spec.id) }
                        )
                    }
                )
            }
        }
    }
}

/**
 * 购物车底部结算栏
 *
 * @param isCheckAll 是否全选
 * @param isEditing 是否处于编辑模式
 * @param selectedCount 已选商品数量
 * @param totalPrice 已选商品总金额
 * @param onCheckAllChanged 切换全选状态回调
 * @param onDeleteClick 删除按钮点击回调
 * @param onSettleClick 结算按钮点击回调
 * @param modifier 修饰符
 * @author Joker.X
 */
@Composable
private fun CartBottomBar(
    isCheckAll: Boolean,
    isEditing: Boolean,
    selectedCount: Int,
    totalPrice: Int,
    onCheckAllChanged: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacePaddingMedium)
        ) {
            CheckButton(
                selected = isCheckAll,
                onClick = onCheckAllChanged
            )

            SpaceHorizontalSmall()

            Text(text = stringResource(id = R.string.select_all))

            Spacer(modifier = Modifier.weight(1f))

            if (!isEditing) {
                Text(text = stringResource(id = R.string.total))
                SpaceHorizontalSmall()
                PriceText(totalPrice)
                SpaceHorizontalSmall()
                AppButtonFixed(
                    text = if (selectedCount == 0) stringResource(id = R.string.settle_account) else stringResource(
                        id = R.string.settle_account_count,
                        selectedCount
                    ),
                    onClick = onSettleClick,
                    enabled = selectedCount > 0,
                    size = ButtonSize.MINI,
                    type = ButtonType.DEFAULT,
                    shape = ButtonShape.ROUND,
                    modifier = Modifier
                        .widthIn(min = 90.dp)
                )
            } else {
                AppButtonFixed(
                    text = if (selectedCount == 0) stringResource(id = R.string.delete) else stringResource(
                        id = R.string.delete_count,
                        selectedCount
                    ),
                    onClick = onDeleteClick,
                    enabled = selectedCount > 0,
                    size = ButtonSize.MINI,
                    type = ButtonType.DANGER,
                    shape = ButtonShape.ROUND,
                    modifier = Modifier
                        .widthIn(min = 90.dp)
                )
            }
        }
    }
}




