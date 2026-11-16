package com.joker.coolmallkmp.feature.goods

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.feature.main.component.CenterTopAppBar
import com.joker.coolmallkmp.core.model.entity.Comment
import com.joker.coolmallkmp.core.model.entity.Goods
import com.joker.coolmallkmp.core.model.entity.GoodsDetail
import com.joker.coolmallkmp.core.model.entity.GoodsSpec
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonBordered
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import com.joker.coolmallkmp.core.ui.component.PriceTextLarge
import com.joker.coolmallkmp.core.ui.component.TitleWithLine
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.core.ui.component.DetailSkeleton
import com.joker.coolmallkmp.core.ui.component.CouponCard
import com.joker.coolmallkmp.core.ui.component.CouponCardMode
import com.joker.coolmallkmp.core.ui.component.AppCard
import com.joker.coolmallkmp.core.ui.component.Modal
import com.joker.coolmallkmp.core.ui.component.Swiper
import com.joker.coolmallkmp.core.ui.component.RefreshLayout
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend
import com.joker.coolmallkmp.core.ui.theme.ColorDanger
import com.joker.coolmallkmp.core.ui.theme.SpaceHorizontalMedium
import com.joker.coolmallkmp.core.ui.theme.SpacePaddingLarge
import com.joker.coolmallkmp.core.ui.theme.SpaceVerticalMedium
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.goods_add_to_cart
import coolmallkmp.shared.generated.resources.goods_available_coupons
import coolmallkmp.shared.generated.resources.goods_buy_now
import coolmallkmp.shared.generated.resources.goods_content_detail
import coolmallkmp.shared.generated.resources.goods_coupon_full_reduce
import coolmallkmp.shared.generated.resources.goods_detail_title
import coolmallkmp.shared.generated.resources.goods_no_image
import coolmallkmp.shared.generated.resources.goods_rating
import coolmallkmp.shared.generated.resources.goods_select_spec
import coolmallkmp.shared.generated.resources.goods_sold
import coolmallkmp.shared.generated.resources.goods_spec
import coolmallkmp.shared.generated.resources.goods_user_comments
import coolmallkmp.shared.generated.resources.goods_view_all_comments
import coolmallkmp.shared.generated.resources.me_user_fallback
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsDetailRoute(
    goodsId: Long,
    onBack: () -> Unit,
    onBuyNow: () -> Unit = {},
    onCommentsClick: (Long) -> Unit = {},
    viewModel: GoodsDetailViewModel = viewModel(
        key = "goods_detail_$goodsId",
        factory = viewModelFactory {
            initializer {
                val container = AppContainerProvider.instance
                GoodsDetailViewModel(
                    goodsId = goodsId,
                    pageRepository = container.pageRepository,
                    goodsRepository = container.goodsRepository,
                    cartRepository = container.cartRepository,
                    footprintRepository = container.footprintRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAdding by viewModel.isAdding.collectAsState()
    val specModalVisible by viewModel.specModalVisible.collectAsState()
    val specsUiState by viewModel.specsUiState.collectAsState()
    val selectedSpec by viewModel.selectedSpec.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    var couponsVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            snackbarHostState.showSnackbar(resolveUiMessageSuspend(msg))
        }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.goods_detail_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState is GoodsDetailUiState.Success) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                            .navigationBarsPadding()
                        .padding(
                            horizontal = SpacePaddingLarge,
                            vertical = SpaceVerticalMedium,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(SpaceHorizontalMedium),
                ) {
                    AppButtonBordered(
                        text = stringResource(Res.string.goods_add_to_cart),
                        onClick = viewModel::showSpecModal,
                        modifier = Modifier.weight(1f),
                        size = AppButtonSize.MEDIUM,
                        enabled = !isAdding,
                        fullWidth = true,
                    )
                    AppButton(
                        text = stringResource(Res.string.goods_buy_now),
                        onClick = viewModel::showSpecModal,
                        modifier = Modifier.weight(1f),
                        size = AppButtonSize.MEDIUM,
                        style = AppButtonStyle.GRADIENT,
                        enabled = !isAdding,
                        fullWidth = true,
                    )
                }
            }
        }
    ) { padding ->
        when (val state = uiState) {
            GoodsDetailUiState.Loading -> {
                DetailSkeleton(Modifier.padding(padding))
            }
            is GoodsDetailUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::loadDetail,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            is GoodsDetailUiState.Success -> {
                GoodsDetailScreen(
                    detail = state.data,
                    selectedSpec = selectedSpec,
                    onShowSpecModal = viewModel::showSpecModal,
                    onShowCouponModal = { couponsVisible = true },
                    onCommentsClick = { onCommentsClick(goodsId) },
                    onRefresh = viewModel::loadDetail,
                    modifier = Modifier.padding(padding),
                )
                if (specModalVisible) {
                    SpecSelectSheet(
                        goods = state.data.goodsInfo,
                        specsUiState = specsUiState,
                        selectedSpec = selectedSpec,
                        quantity = quantity,
                        isAdding = isAdding,
                        onDismiss = viewModel::hideSpecModal,
                        onRetry = { viewModel.loadGoodsSpecs(force = true) },
                        onSpecSelected = viewModel::selectSpec,
                        onIncrease = viewModel::increaseQuantity,
                        onDecrease = viewModel::decreaseQuantity,
                        onAddToCart = viewModel::confirmAddToCart,
                        onBuyNow = {
                            if (viewModel.buyNow()) {
                                onBuyNow()
                            }
                        },
                    )
                }
                if (couponsVisible) {
                    Modal(
                        visible = true,
                        onDismiss = { couponsVisible = false },
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            Text(
                                text = stringResource(Res.string.goods_available_coupons),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(Modifier.height(12.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 500.dp),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(state.data.coupon, key = { it.id }) { coupon ->
                                    CouponCard(
                                        coupon = coupon,
                                        mode = CouponCardMode.RECEIVE,
                                        onActionClick = {},
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoodsDetailScreen(
    detail: GoodsDetail,
    selectedSpec: com.joker.coolmallkmp.core.model.entity.GoodsSpec?,
    onShowSpecModal: () -> Unit,
    onShowCouponModal: () -> Unit,
    onCommentsClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val goods = detail.goodsInfo
    RefreshLayout(
        modifier = modifier.fillMaxSize(),
        listState = rememberLazyListState(),
        onRefresh = onRefresh,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            GoodsGallery(goods = goods)
        }
        item {
            GoodsInfoSection(goods = goods, selectedSpec = selectedSpec)
        }
        item {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onShowSpecModal),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.goods_spec),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = selectedSpec?.name?.takeIf { it.isNotBlank() }
                            ?: stringResource(Res.string.goods_select_spec),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        if (detail.coupon.isNotEmpty()) {
            item {
                    TitleWithLine(
                        text = stringResource(Res.string.goods_available_coupons),
                        modifier = Modifier.clickable(onClick = onShowCouponModal),
                    )
            }
            items(detail.coupon, key = { it.id }) { coupon ->
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = coupon.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = stringResource(
                                Res.string.goods_coupon_full_reduce,
                                coupon.condition?.fullAmount ?: 0,
                                coupon.amount,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorDanger,
                        )
                    }
                }
            }
        }
        if (detail.comment.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCommentsClick),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TitleWithLine(
                        text = stringResource(Res.string.goods_user_comments),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(Res.string.goods_view_all_comments),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            items(detail.comment, key = { it.id }) { comment ->
                CommentItem(comment = comment)
            }
        }
        val contentPics = goods.contentPics.orEmpty()
        if (contentPics.isNotEmpty()) {
            item {
                TitleWithLine(text = stringResource(Res.string.goods_content_detail))
            }
            items(contentPics) { pic ->
                AsyncImage(
                    model = pic,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
private fun GoodsGallery(goods: Goods) {
    val pics = buildList {
        if (goods.mainPic.isNotBlank()) add(goods.mainPic)
        goods.pics.orEmpty().forEach { if (it !in this) add(it) }
    }
    if (pics.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(Res.string.goods_no_image))
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Swiper(
            items = pics,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            autoplay = false,
        ) { _, pic ->
            AsyncImage(
                model = pic,
                contentDescription = goods.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun GoodsInfoSection(
    goods: Goods,
    selectedSpec: GoodsSpec?,
) {
    val displayPrice = selectedSpec?.price?.takeIf { it > 0 } ?: goods.price
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PriceTextLarge(price = displayPrice)
        Text(
            text = goods.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        goods.subTitle?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = stringResource(Res.string.goods_sold, goods.sold),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = comment.avatarUrl,
                    contentDescription = comment.nickName,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comment.nickName ?: stringResource(Res.string.me_user_fallback),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = stringResource(Res.string.goods_rating, comment.starCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
            )
            val pics = comment.pics.orEmpty()
            if (pics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pics) { pic ->
                        AsyncImage(
                            model = pic,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }
    }
}
