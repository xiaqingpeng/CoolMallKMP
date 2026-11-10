package com.joker.coolmall.feature.goods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.ui.component.GoodsGridItem
import com.joker.coolmall.core.ui.empty.EmptyData
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_no_more
import myapplication.shared.generated.resources.goods_list_default
import myapplication.shared.generated.resources.goods_list_empty
import myapplication.shared.generated.resources.goods_list_flash
import myapplication.shared.generated.resources.goods_list_recommend
import myapplication.shared.generated.resources.goods_list_search_prefix
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
    val gridState = rememberLazyGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = gridState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 4
        }
    }

    LaunchedEffect(shouldLoadMore, uiState) {
        val success = uiState as? GoodsCategoryUiState.Success
        if (shouldLoadMore && success?.hasMore == true && !isLoadingMore) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val resolvedTitle = viewModel.routeTitle.ifBlank {
                        when {
                            !viewModel.listKeyword.isNullOrBlank() ->
                                stringResource(Res.string.goods_list_search_prefix, viewModel.listKeyword!!)
                            viewModel.listFeatured -> stringResource(Res.string.goods_list_flash)
                            viewModel.listRecommend -> stringResource(Res.string.goods_list_recommend)
                            else -> stringResource(Res.string.goods_list_default)
                        }
                    }
                    Text(resolvedTitle)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            GoodsCategoryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
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
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                when {
                                    isLoadingMore -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    !state.hasMore -> Text(
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
    }
}
