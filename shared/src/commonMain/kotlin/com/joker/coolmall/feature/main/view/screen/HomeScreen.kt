package com.joker.coolmallkmp.feature.main.view.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.theme.PrimaryDefault
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.model.entity.Banner
import com.joker.coolmallkmp.core.model.entity.Category
import com.joker.coolmallkmp.core.model.entity.Coupon
import com.joker.coolmallkmp.core.model.entity.Goods
import com.joker.coolmallkmp.core.model.entity.Home
import com.joker.coolmallkmp.core.ui.component.CouponCard
import com.joker.coolmallkmp.core.ui.component.CouponCardMode
import com.joker.coolmallkmp.core.ui.component.GoodsGridItem
import com.joker.coolmallkmp.core.ui.component.GoodsListItem
import com.joker.coolmallkmp.core.ui.component.PriceTextSmall
import com.joker.coolmallkmp.core.ui.component.TitleWithLine
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend
import com.joker.coolmallkmp.feature.main.viewmodel.HomeUiState
import com.joker.coolmallkmp.feature.main.viewmodel.HomeViewModel
import com.joker.coolmallkmp.feature.main.view.skeleton.HomeLoadingSkeleton
import com.joker.coolmallkmp.feature.main.component.CommonScaffold
import kotlinx.coroutines.delay
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_no_more
import coolmallkmp.shared.generated.resources.coupon_list_title
import coolmallkmp.shared.generated.resources.coupon_receive
import coolmallkmp.shared.generated.resources.home_all_goods
import coolmallkmp.shared.generated.resources.home_flash_sale
import coolmallkmp.shared.generated.resources.home_recommend
import coolmallkmp.shared.generated.resources.home_search_hint
import coolmallkmp.shared.generated.resources.ic_coupon
import coolmallkmp.shared.generated.resources.ic_github
import coolmallkmp.shared.generated.resources.ic_logo
import coolmallkmp.shared.generated.resources.ic_search
import coolmallkmp.shared.generated.resources.ic_time
import coolmallkmp.shared.generated.resources.me_view_all
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 首页路由：回调导航 + HomeViewModel（对齐 CoolMallKotlin 结构，去掉 Hilt/Navigator）
 */
@Composable
fun HomeRoute(
    onGoodsClick: (Long) -> Unit = {},
    onCategoryClick: (Long, String) -> Unit = { _, _ -> },
    onSearchClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onGithubClick: () -> Unit = {},
    onFeaturedClick: () -> Unit = {},
    onLoginRequired: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val c = AppContainerProvider.instance
                HomeViewModel(
                    pageRepository = c.pageRepository,
                    goodsRepository = c.goodsRepository,
                    couponRepository = c.couponRepository,
                    authRepository = c.authRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val pageData by viewModel.pageData.collectAsState()
    val listData by viewModel.listData.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val couponModalVisible by viewModel.couponModalVisible.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { msg ->
            snackbarHostState.showSnackbar(resolveUiMessageSuspend(msg))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.loginRequired.collect {
            onLoginRequired()
        }
    }

    when (val state = uiState) {
        HomeUiState.Loading -> {
            HomeLoadingSkeleton()
        }

        is HomeUiState.Error -> {
            EmptyUiMessage(
                message = state.message,
                onRetryClick = viewModel::loadHomeData,
                modifier = Modifier.fillMaxSize(),
            )
        }

        HomeUiState.Success -> {
            HomeScreen(
                pageData = pageData,
                listData = listData,
                isRefreshing = isRefreshing,
                isLoadingMore = isLoadingMore,
                hasMore = hasMore,
                couponModalVisible = couponModalVisible,
                snackbarHostState = snackbarHostState,
                onGoodsClick = onGoodsClick,
                onCategoryClick = onCategoryClick,
                onSearchClick = onSearchClick,
                onAboutClick = onAboutClick,
                onGithubClick = onGithubClick,
                onFeaturedClick = onFeaturedClick,
                onRefresh = viewModel::onRefresh,
                onLoadMore = viewModel::onLoadMore,
                onShowCouponModal = viewModel::showCouponModal,
                onHideCouponModal = viewModel::hideCouponModal,
                onCouponReceive = viewModel::receiveCoupon,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    pageData: Home,
    listData: List<Goods>,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    couponModalVisible: Boolean,
    snackbarHostState: SnackbarHostState,
    onGoodsClick: (Long) -> Unit,
    onCategoryClick: (Long, String) -> Unit,
    onSearchClick: () -> Unit,
    onAboutClick: () -> Unit,
    onGithubClick: () -> Unit,
    onFeaturedClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onShowCouponModal: () -> Unit,
    onHideCouponModal: () -> Unit,
    onCouponReceive: (Coupon) -> Unit,

) {
    // 对齐 CoolMallKotlin RefreshLayout(isGrid = true)：瀑布流 + 下拉刷新 + 顶栏联动
    val gridState = rememberLazyStaggeredGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = gridState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 4
        }
    }

    LaunchedEffect(shouldLoadMore, hasMore, isLoadingMore, isRefreshing) {
        if (shouldLoadMore && hasMore && !isLoadingMore && !isRefreshing) {
            onLoadMore()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CommonScaffold(
            topBar = {
                HomeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onSearchClick = onSearchClick,
                    onAboutClick = onAboutClick,
                    onGithubClick = onGithubClick,
                )
            },
            scrollBehavior = scrollBehavior,
        ) { padding ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                state = pullToRefreshState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                ) {
                    val banners = pageData.banner.orEmpty()
                    if (banners.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            BannerSection(banners = banners)
                        }
                    }

                    val coupons = pageData.coupon.orEmpty()
                    if (coupons.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            CouponSection(
                                coupons = coupons,
                                onShowCouponModal = onShowCouponModal,
                            )
                        }
                    }

                    val categories = pageData.category.orEmpty()
                    if (categories.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            CategorySection(
                                categories = categories,
                                onCategoryClick = onCategoryClick,
                            )
                        }
                    }

                    val flashSale = pageData.flashSale.orEmpty()
                    if (flashSale.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            FlashSaleSection(
                                goods = flashSale,
                                onGoodsClick = onGoodsClick,
                                onViewAllClick = onFeaturedClick,
                            )
                        }
                    }

                    val recommend = pageData.recommend.orEmpty()
                    if (recommend.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            TitleWithLine(
                                text = stringResource(Res.string.home_recommend),
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                recommend.forEach { goods ->
                                    GoodsListItem(
                                        goods = goods,
                                        onClick = { onGoodsClick(goods.id) },
                                    )
                                }
                            }
                        }
                    }

                    if (listData.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            TitleWithLine(
                                text = stringResource(Res.string.home_all_goods),
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                        items(
                            items = listData,
                            key = { it.id },
                        ) { goods ->
                            GoodsGridItem(
                                goods = goods,
                                onClick = { onGoodsClick(goods.id) },
                            )
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            when {
                                isLoadingMore -> {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                                !hasMore && listData.isNotEmpty() -> {
                                    Text(
                                        text = stringResource(Res.string.common_no_more),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
        )
    }

    if (couponModalVisible) {
        CouponReceiveSheet(
            coupons = pageData.coupon.orEmpty(),
            onDismiss = onHideCouponModal,
            onCouponReceive = onCouponReceive,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onSearchClick: () -> Unit,
    onAboutClick: () -> Unit,
    onGithubClick: () -> Unit,
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onAboutClick) {
                Image(
                    painter = painterResource(Res.drawable.ic_logo),
                    contentDescription = null,
                    modifier = Modifier.size(34.dp),
                )
            }
        },
        title = {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clickable(onClick = onSearchClick),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = stringResource(Res.string.home_search_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onGithubClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_github),
                    contentDescription = "GitHub",
                    modifier = Modifier.size(22.dp),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun BannerSection(
    banners: List<Banner>,
    autoplay: Boolean = true,
    intervalMs: Long = 3000,
) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState { banners.size }

    // 对齐 CoolMallKotlin WeSwiper：自动轮播（默认 3s）
    if (autoplay && banners.size > 1) {
        LaunchedEffect(pagerState.settledPage, banners.size) {
            delay(intervalMs)
            if (pagerState.pageCount > 0) {
                val targetPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            AsyncImage(
                model = banners[page].pic,
                contentDescription = banners[page].description,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        // 对齐 WeSwiperDefaults.Indicator：底部横向进度条
        if (banners.size > 1) {
            BannerIndicator(
                count = banners.size,
                current = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-10).dp),
            )
        }
    }
}

/**
 * 轮播指示器（对齐 CoolMallKotlin WeSwiperDefaults.Indicator）
 */
@Composable
private fun BannerIndicator(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
) {
    if (count <= 1) return
    val safeCurrent = current.coerceIn(0, count - 1)
    Row(
        modifier = modifier
            .width((count * 16).dp)
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(PrimaryDefault.copy(alpha = 0.2f)),
    ) {
        val offsetX by animateDpAsState(
            targetValue = (safeCurrent * 16).dp,
            label = "BannerIndicatorOffset",
        )
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(3.dp)
                .offset(x = offsetX)
                .clip(RoundedCornerShape(2.dp))
                .background(PrimaryDefault.copy(alpha = 0.6f)),
        )
    }
}

@Composable
private fun CouponSection(
    coupons: List<Coupon>,
    onShowCouponModal: () -> Unit,
) {
    val pagerState = rememberPagerState { coupons.size.coerceAtLeast(1) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (coupons.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = coupons.size > 1,
                    ) { page ->
                        HomeCouponHint(coupon = coupons[page])
                    }
                }
            }
            VerticalDivider(
                modifier = Modifier
                    .height(20.dp)
                    .padding(horizontal = 12.dp),
                thickness = 0.5.dp,
            )
            Text(
                text = stringResource(Res.string.coupon_receive),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onShowCouponModal),
            )
        }
    }
}

@Composable
private fun HomeCouponHint(coupon: Coupon) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(4.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_coupon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            )
        }
        Text(
            text = coupon.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        )
    }
}

@Composable
private fun CategorySection(
    categories: List<Category>,
    onCategoryClick: (Long, String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            categories.chunked(5).forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    rowCategories.forEach { category ->
                        CategoryItem(
                            category = category,
                            onClick = { onCategoryClick(category.id, category.name) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(5 - rowCategories.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
    ) {
        AsyncImage(
            model = category.pic,
            contentDescription = category.name,
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FlashSaleSection(
    goods: List<Goods>,
    onGoodsClick: (Long) -> Unit,
    onViewAllClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onViewAllClick)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_time),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.home_flash_sale),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(Res.string.me_view_all),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider()
            LazyRow(
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(goods, key = { it.id }) { item ->
                    FlashSaleItem(
                        goods = item,
                        onClick = { onGoodsClick(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashSaleItem(
    goods: Goods,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = goods.mainPic,
            contentDescription = goods.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = goods.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            minLines = 2,
        )
        Spacer(modifier = Modifier.height(2.dp))
        PriceTextSmall(price = goods.price)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CouponReceiveSheet(
    coupons: List<Coupon>,
    onDismiss: () -> Unit,
    onCouponReceive: (Coupon) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.coupon_list_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            coupons.forEach { coupon ->
                CouponCard(
                    coupon = coupon,
                    mode = CouponCardMode.RECEIVE,
                    onActionClick = { onCouponReceive(coupon) },
                )
            }
            if (coupons.isEmpty()) {
                Text(
                    text = stringResource(Res.string.common_no_more),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }
        }
    }
}
