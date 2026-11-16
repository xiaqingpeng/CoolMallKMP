package com.joker.coolmallkmp.core.ui.component.refresh

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joker.coolmallkmp.core.common.base.state.LoadMoreState
import com.joker.coolmallkmp.core.ui.component.RefreshLayout as SharedRefreshLayout

@Composable
fun RefreshLayout(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    isRefreshing: Boolean = false,
    loadMoreState: LoadMoreState = LoadMoreState.PullToLoad,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    content: LazyListScope.() -> Unit,
) = SharedRefreshLayout(
    modifier = modifier,
    listState = listState,
    isRefreshing = isRefreshing,
    loadMoreState = loadMoreState,
    onRefresh = onRefresh,
    onLoadMore = onLoadMore,
    content = content,
)
