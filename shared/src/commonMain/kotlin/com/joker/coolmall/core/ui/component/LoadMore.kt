package com.joker.coolmall.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.common.base.state.LoadMoreState

@Composable
fun LoadMore(
    state: LoadMoreState,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (state) {
            LoadMoreState.Loading -> {
                CircularProgressIndicator(strokeWidth = 2.dp)
                Text("正在加载", modifier = Modifier.padding(start = 8.dp))
            }
            LoadMoreState.Error -> Text(
                "加载失败，点击重试",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.clickable(enabled = onRetry != null) { onRetry?.invoke() },
            )
            LoadMoreState.NoMore -> {
                HorizontalDivider(Modifier.weight(1f))
                Text("没有更多了", modifier = Modifier.padding(horizontal = 12.dp))
                HorizontalDivider(Modifier.weight(1f))
            }
            LoadMoreState.PullToLoad -> Text("上拉加载更多", color = MaterialTheme.colorScheme.onSurfaceVariant)
            LoadMoreState.Success -> Text("加载完成", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ReverseLoadMore(
    state: LoadMoreState,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    // reverseLayout 会把 footer 视觉上放到列表顶部，因此文案仍描述历史消息方向。
    LoadMore(state = state, modifier = modifier, onRetry = onRetry)
}
