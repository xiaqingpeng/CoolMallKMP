package com.joker.coolmall.core.ui.component.loading

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joker.coolmall.core.common.base.state.LoadMoreState
import com.joker.coolmall.core.ui.component.LoadMore as SharedLoadMore

@Composable
fun LoadMore(
    modifier: Modifier = Modifier,
    state: LoadMoreState = LoadMoreState.PullToLoad,
    onRetry: (() -> Unit)? = null,
) = SharedLoadMore(state = state, modifier = modifier, onRetry = onRetry)
