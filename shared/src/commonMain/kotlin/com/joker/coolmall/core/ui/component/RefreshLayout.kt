package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.common.base.state.LoadMoreState
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.refresh_complete
import coolmallkmp.shared.generated.resources.refresh_pull_down
import coolmallkmp.shared.generated.resources.refresh_refreshing
import coolmallkmp.shared.generated.resources.refresh_release
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

private val RefreshHeaderHeight = 52.dp

@Composable
fun RefreshLayout(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    isRefreshing: Boolean = false,
    loadMoreState: LoadMoreState = LoadMoreState.PullToLoad,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: LazyListScope.() -> Unit,
) {
    val refreshState = rememberRefreshState()
    SyncRefreshState(refreshState, isRefreshing, onRefresh)
    val reachedEnd by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            info.totalItemsCount > 0 &&
                (info.visibleItemsInfo.lastOrNull()?.index ?: -1) >= info.totalItemsCount - 3
        }
    }
    LoadMoreEffect(reachedEnd, loadMoreState, onLoadMore)
    RefreshContainer(modifier, refreshState) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            content = content,
        )
    }
}

@Composable
fun RefreshGridLayout(
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    isRefreshing: Boolean = false,
    loadMoreState: LoadMoreState = LoadMoreState.PullToLoad,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: LazyGridScope.() -> Unit,
) {
    val refreshState = rememberRefreshState()
    SyncRefreshState(refreshState, isRefreshing, onRefresh)
    val reachedEnd by remember {
        derivedStateOf {
            val info = gridState.layoutInfo
            info.totalItemsCount > 0 &&
                (info.visibleItemsInfo.lastOrNull()?.index ?: -1) >= info.totalItemsCount - 3
        }
    }
    LoadMoreEffect(reachedEnd, loadMoreState, onLoadMore)
    RefreshContainer(modifier, refreshState) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            content = content,
        )
    }
}

@Composable
fun RefreshStaggeredGridLayout(
    modifier: Modifier = Modifier,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    isRefreshing: Boolean = false,
    loadMoreState: LoadMoreState = LoadMoreState.PullToLoad,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    verticalItemSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: LazyStaggeredGridScope.() -> Unit,
) {
    val refreshState = rememberRefreshState()
    SyncRefreshState(refreshState, isRefreshing, onRefresh)
    val reachedEnd by remember {
        derivedStateOf {
            val info = gridState.layoutInfo
            info.totalItemsCount > 0 &&
                (info.visibleItemsInfo.lastOrNull()?.index ?: -1) >= info.totalItemsCount - 3
        }
    }
    LoadMoreEffect(reachedEnd, loadMoreState, onLoadMore)
    RefreshContainer(modifier, refreshState) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            horizontalArrangement = horizontalArrangement,
            verticalItemSpacing = verticalItemSpacing,
            content = content,
        )
    }
}

@Composable
private fun SyncRefreshState(
    state: RefreshState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    state.onRefresh = onRefresh
    LaunchedEffect(isRefreshing) {
        if (isRefreshing && !state.isRefreshing) {
            state.isRefreshing = true
            state.isFinishing = false
            onRefresh()
        } else if (!isRefreshing && state.isRefreshing) {
            state.isRefreshing = false
            state.isFinishing = state.indicatorOffset > 0f
            if (state.isFinishing) delay(800)
            state.isFinishing = false
            state.animateOffsetTo(0f)
        }
    }
}

@Composable
private fun LoadMoreEffect(
    reachedEnd: Boolean,
    state: LoadMoreState,
    onLoadMore: () -> Unit,
) {
    LaunchedEffect(reachedEnd, state) {
        if (
            reachedEnd &&
            state != LoadMoreState.Loading &&
            state != LoadMoreState.NoMore &&
            state != LoadMoreState.Success
        ) {
            onLoadMore()
        }
    }
}

@Composable
private fun RefreshContainer(
    modifier: Modifier,
    state: RefreshState,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier.fillMaxSize().clipToBounds().nestedScroll(state.connection),
        content = {
            content()
            RefreshHeader(
                modifier = Modifier.fillMaxWidth().height(RefreshHeaderHeight),
                state = state,
            )
        },
    ) { measurables, constraints ->
        val contentPlaceable = measurables[0].measure(constraints)
        val headerPlaceable = measurables[1].measure(
            constraints.copy(minHeight = 0, maxHeight = constraints.maxHeight),
        )
        state.headerHeight = headerPlaceable.height.toFloat()
        layout(constraints.maxWidth, constraints.maxHeight) {
            val offset = state.indicatorOffset.roundToInt()
            contentPlaceable.placeRelative(0, offset)
            headerPlaceable.placeRelative(0, -headerPlaceable.height + offset)
        }
    }
}

@Composable
private fun RefreshHeader(
    modifier: Modifier,
    state: RefreshState,
) {
    val text = when {
        state.isRefreshing -> stringResource(Res.string.refresh_refreshing)
        state.isFinishing -> stringResource(Res.string.refresh_complete)
        state.indicatorOffset >= state.headerHeight -> stringResource(Res.string.refresh_release)
        else -> stringResource(Res.string.refresh_pull_down)
    }
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                if (state.isRefreshing || state.isFinishing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = color,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Canvas(Modifier.size(20.dp).border(1.5.dp, color, CircleShape)) {
                        drawCircle(
                            color = color,
                            radius = 2.5.dp.toPx(),
                            center = Offset(size.width / 2f, 2.5.dp.toPx()),
                        )
                    }
                }
            }
            Text(text = text, color = color, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun rememberRefreshState(): RefreshState {
    val scope = rememberCoroutineScope()
    return remember { RefreshState(scope) }
}

@Stable
private class RefreshState(
    private val scope: CoroutineScope,
) {
    var headerHeight by mutableStateOf(0f)
    var indicatorOffset by mutableStateOf(0f)
    var isRefreshing by mutableStateOf(false)
    var isFinishing by mutableStateOf(false)
    var onRefresh: () -> Unit = {}

    fun animateOffsetTo(target: Float) {
        scope.launch {
            val start = indicatorOffset
            repeat(12) { index ->
                indicatorOffset = start + (target - start) * (index + 1) / 12
                delay(16)
            }
        }
    }

    private fun consume(delta: Float) {
        indicatorOffset = (indicatorOffset + delta).coerceAtLeast(0f)
    }

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y < 0f && indicatorOffset > 0f) {
                val consumed = (available.y * 0.5f).coerceAtLeast(-indicatorOffset)
                consume(consumed)
                return Offset(0f, consumed / 0.5f)
            }
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (available.y > 0f && !isRefreshing) {
                val consumed = available.y * 0.5f
                consume(consumed)
                return Offset(0f, consumed / 0.5f)
            }
            return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (indicatorOffset >= headerHeight && !isRefreshing) {
                isRefreshing = true
                onRefresh()
                animateOffsetTo(headerHeight)
                return available
            }
            if (indicatorOffset > 0f) animateOffsetTo(0f)
            return Velocity.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (!isRefreshing && indicatorOffset > 0f) animateOffsetTo(0f)
            return available
        }
    }
}
