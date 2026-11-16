package com.joker.coolmallkmp.feature.goods.view.screen
import com.joker.coolmallkmp.feature.goods.viewmodel.GoodsCategoryViewModel
import com.joker.coolmallkmp.feature.goods.viewmodel.*

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as columnItems
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.ui.component.GoodsGridItem
import com.joker.coolmallkmp.core.ui.component.GoodsListItem
import com.joker.coolmallkmp.core.ui.component.GoodsGridSkeleton
import com.joker.coolmallkmp.core.ui.component.RefreshGridLayout
import com.joker.coolmallkmp.core.ui.component.RefreshLayout
import com.joker.coolmallkmp.core.ui.component.SkeletonBlock
import com.joker.coolmallkmp.core.ui.empty.EmptyData
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.common_no_more
import coolmallkmp.shared.generated.resources.goods_list_default
import coolmallkmp.shared.generated.resources.goods_list_empty
import coolmallkmp.shared.generated.resources.goods_list_flash
import coolmallkmp.shared.generated.resources.goods_list_recommend
import coolmallkmp.shared.generated.resources.goods_list_search_prefix
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsCategoryRoute(
    typeId: Long?,
    title: String,
    featured: Boolean = false,
    recommend: Boolean = false,
    keyword: String? = null,
    onBack: () -> Unit,
    onGoodsClick: (Long) -> Unit,
    onSearch: (String) -> Unit = {},
    viewModel: GoodsCategoryViewModel = viewModel(
        key = "goods_category_${typeId}_${featured}_${recommend}_${keyword}",
        factory = viewModelFactory {
            initializer {
                GoodsCategoryViewModel(
                    typeId = typeId,
                    title = title,
                    featured = featured,
                    recommend = recommend,
                    keyword = keyword,
                    goodsRepository = AppContainerProvider.instance.goodsRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val filtersVisible by viewModel.filtersVisible.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val filterUiState by viewModel.filterUiState.collectAsState()
    val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsState()
    val minPrice by viewModel.minPrice.collectAsState()
    val maxPrice by viewModel.maxPrice.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val sortState by viewModel.sortState.collectAsState()
    var isGrid by remember { mutableStateOf(true) }
    var searchText by rememberSaveable { mutableStateOf(keyword.orEmpty()) }
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = gridState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 4
        }
    }
    val shouldLoadMoreList by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 4
        }
    }

    LaunchedEffect(shouldLoadMore, shouldLoadMoreList, uiState, isGrid) {
        val success = uiState as? GoodsCategoryUiState.Success
        if ((if (isGrid) shouldLoadMore else shouldLoadMoreList) &&
            success?.hasMore == true &&
            !isLoadingMore
        ) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.statusBarsPadding(),
            ) {
                TopAppBar(
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "搜索",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp),
                            )
                            BasicTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        searchText.trim().takeIf(String::isNotEmpty)?.let {
                                            keyboard?.hide()
                                            onSearch(it)
                                        }
                                    },
                                ),
                                decorationBox = { innerTextField ->
                                    if (searchText.isBlank()) {
                                        Text(
                                            viewModel.routeTitle.ifBlank {
                                                when {
                                                    viewModel.listFeatured -> stringResource(Res.string.goods_list_flash)
                                                    viewModel.listRecommend -> stringResource(Res.string.goods_list_recommend)
                                                    else -> stringResource(Res.string.goods_list_default)
                                                }
                                            },
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    innerTextField()
                                },
                            )
                            if (searchText.isNotBlank()) {
                                IconButton(
                                    onClick = { searchText = "" },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "清除", modifier = Modifier.size(18.dp))
                                }
                            }
                            Text(
                                text = "搜索",
                                color = Color.White,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable(
                                        enabled = searchText.trim().isNotEmpty(),
                                        onClick = {
                                            searchText.trim().takeIf(String::isNotEmpty)?.let {
                                                keyboard?.hide()
                                                onSearch(it)
                                            }
                                        },
                                    )
                                    .padding(horizontal = 18.dp, vertical = 8.dp),
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.common_back),
                            )
                        }
                    },
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                CategoryFilterButton(
                    selected = filtersVisible,
                    onClick = viewModel::showFilters,
                    leadingIcon = { Icon(Icons.Default.FilterList, null, modifier = Modifier.size(14.dp)) },
                )
                Spacer(Modifier.width(8.dp))
                CategoryFilterButton(
                    selected = sortType == GoodsSortType.COMPREHENSIVE,
                    onClick = { viewModel.cycleSort(GoodsSortType.COMPREHENSIVE) },
                    label = "综合",
                )
                Spacer(Modifier.width(8.dp))
                CategoryFilterButton(
                    selected = sortType == GoodsSortType.SALES,
                    onClick = { viewModel.cycleSort(GoodsSortType.SALES) },
                    label = "销量${sortStateLabel(sortType, sortState)}",
                )
                Spacer(Modifier.width(8.dp))
                CategoryFilterButton(
                    selected = sortType == GoodsSortType.PRICE,
                    onClick = { viewModel.cycleSort(GoodsSortType.PRICE) },
                    label = "价格${sortStateLabel(sortType, sortState)}",
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { isGrid = !isGrid }) {
                    Icon(
                        if (isGrid) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = null,
                    )
                }
                IconButton(onClick = viewModel::refresh) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                }
                }
            }
        }
    ) { padding ->
        when (val state = uiState) {
            GoodsCategoryUiState.Loading -> {
                GoodsGridSkeleton(modifier = Modifier.padding(padding))
            }

            is GoodsCategoryUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::refresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is GoodsCategoryUiState.Success -> {
                if (state.goods.isEmpty()) {
                    EmptyData(
                        message = stringResource(Res.string.goods_list_empty),
                        onRetryClick = viewModel::refresh,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    )
                } else {
                    if (isGrid) {
                        RefreshGridLayout(
                            gridState = gridState,
                            isRefreshing = uiState is GoodsCategoryUiState.Loading,
                            onRefresh = viewModel::refresh,
                            onLoadMore = viewModel::loadMore,
                            modifier = Modifier.fillMaxSize().padding(padding),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.goods, key = { it.id }) { goods ->
                                GoodsGridItem(
                                    goods = goods,
                                    onClick = { onGoodsClick(goods.id) },
                                )
                            }
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                LoadMoreFooter(isLoadingMore, state.hasMore)
                            }
                        }
                    } else {
                        RefreshLayout(
                            listState = listState,
                            isRefreshing = uiState is GoodsCategoryUiState.Loading,
                            onRefresh = viewModel::refresh,
                            onLoadMore = viewModel::loadMore,
                            modifier = Modifier.fillMaxSize().padding(padding),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            columnItems(state.goods, key = { it.id }) { goods ->
                                GoodsListItem(goods = goods, onClick = { onGoodsClick(goods.id) })
                            }
                            item {
                                LoadMoreFooter(isLoadingMore, state.hasMore)
                            }
                        }
                    }
                }
            }
        }
    }
    if (filtersVisible) {
        FilterDialog(
            categories = categories,
            filterUiState = filterUiState,
            selectedCategoryIds = selectedCategoryIds,
            minPrice = minPrice,
            maxPrice = maxPrice,
            onDismiss = viewModel::hideFilters,
            onApply = viewModel::applyFilters,
            onReset = viewModel::resetFilters,
            onRetry = viewModel::retryFilters,
        )
    }
}

@Composable
private fun LoadMoreFooter(
    isLoadingMore: Boolean,
    hasMore: Boolean,
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isLoadingMore -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
            !hasMore -> Text(
                text = stringResource(Res.string.common_no_more),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun sortStateLabel(type: GoodsSortType, state: GoodsSortState): String =
    if (type == GoodsSortType.COMPREHENSIVE || state == GoodsSortState.NONE) "" else if (state == GoodsSortState.ASC) " ↑" else " ↓"

@Composable
private fun CategoryFilterButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String = "筛选",
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                } else {
                    MaterialTheme.colorScheme.background
                },
            )
            .clickable(onClick = onClick)
            .height(32.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        leadingIcon?.invoke()
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterDialog(
    categories: List<com.joker.coolmallkmp.core.model.entity.Category>,
    filterUiState: GoodsCategoryFilterUiState,
    selectedCategoryIds: List<Long>,
    minPrice: String,
    maxPrice: String,
    onDismiss: () -> Unit,
    onApply: (List<Long>, String, String) -> Unit,
    onReset: () -> Unit,
    onRetry: () -> Unit,
) {
    var selected by remember(selectedCategoryIds) { mutableStateOf(selectedCategoryIds) }
    var min by remember(minPrice) { mutableStateOf(minPrice) }
    var max by remember(maxPrice) { mutableStateOf(maxPrice) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            val bottomInset = with(LocalDensity.current) {
                WindowInsets.navigationBars.getBottom(this).toDp()
            }
            val maxDialogHeight = (maxHeight - bottomInset - 32.dp).coerceAtLeast(320.dp)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = minOf(680.dp, maxDialogHeight))
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp,
            ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 78.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "筛选",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "关闭")
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("价格区间", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                PriceInputField(
                                    value = min,
                                    placeholder = "最低价格",
                                    onValueChange = { min = it.filter(Char::isDigit).take(8) },
                                    modifier = Modifier.weight(1f),
                                )
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outline),
                                )
                                PriceInputField(
                                    value = max,
                                    placeholder = "最高价格",
                                    onValueChange = { max = it.filter(Char::isDigit).take(8) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    when (filterUiState) {
                        GoodsCategoryFilterUiState.Loading -> {
                            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    SkeletonBlock(Modifier.fillMaxWidth(0.28f).height(18.dp))
                                    repeat(3) {
                                        SkeletonBlock(Modifier.fillMaxWidth().height(40.dp))
                                    }
                                }
                            }
                        }
                        is GoodsCategoryFilterUiState.Error -> {
                            EmptyUiMessage(
                                message = filterUiState.message,
                                onRetryClick = onRetry,
                                modifier = Modifier.fillMaxWidth().height(220.dp),
                            )
                        }
                        is GoodsCategoryFilterUiState.Success -> {
                            val roots = categories.filter { it.parentId == null || it.parentId == 0 }
                            val childrenByParent = categories
                                .filter { it.parentId != null && it.parentId != 0 }
                                .groupBy { it.parentId }
                            roots.forEach { root ->
                                val children = childrenByParent[root.id.toInt()].orEmpty()
                                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(root.name, style = MaterialTheme.typography.titleMedium)
                                        Spacer(Modifier.height(10.dp))
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            children.forEach { category ->
                                            CategoryFilterChip(
                                                name = category.name,
                                                selected = category.id in selected,
                                                onClick = {
                                                    selected = if (category.id in selected) {
                                                        selected - category.id
                                                    } else {
                                                        selected + category.id
                                                    }
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                            }
                            val orphanCategories = categories.filter { category ->
                                category.parentId != null &&
                                    roots.none { it.id.toInt() == category.parentId }
                            }
                            if (orphanCategories.isNotEmpty()) {
                                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("其他分类", style = MaterialTheme.typography.titleMedium)
                                        Spacer(Modifier.height(10.dp))
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            orphanCategories.forEach { category ->
                                                CategoryFilterChip(
                                                    name = category.name,
                                                    selected = category.id in selected,
                                                    onClick = {
                                                        selected = if (category.id in selected) {
                                                            selected - category.id
                                                        } else {
                                                            selected + category.id
                                                        }
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    shadowElevation = 4.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                    com.joker.coolmallkmp.core.ui.component.AppButtonBordered(
                        text = "重置",
                        onClick = {
                            selected = selectedCategoryIds
                            min = ""
                            max = ""
                            onReset()
                        },
                        modifier = Modifier.weight(1f),
                        size = com.joker.coolmallkmp.core.ui.component.AppButtonSize.MINI,
                        fullWidth = true,
                    )
                    com.joker.coolmallkmp.core.ui.component.AppButton(
                        text = "确定",
                        onClick = { onApply(selected, min, max) },
                        modifier = Modifier.weight(1f),
                        size = com.joker.coolmallkmp.core.ui.component.AppButtonSize.MINI,
                        style = com.joker.coolmallkmp.core.ui.component.AppButtonStyle.GRADIENT,
                        fullWidth = true,
                    )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun PriceInputField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = CircleShape,
            )
            .clip(CircleShape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                innerTextField()
            },
        )
    }
}

@Composable
private fun CategoryFilterChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}
