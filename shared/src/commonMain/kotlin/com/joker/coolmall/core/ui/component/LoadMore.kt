package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.common.base.state.LoadMoreState
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_load_error_retry
import coolmallkmp.shared.generated.resources.common_load_success
import coolmallkmp.shared.generated.resources.common_loading
import coolmallkmp.shared.generated.resources.common_no_more
import coolmallkmp.shared.generated.resources.cs_load_more
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadMore(
    state: LoadMoreState,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (state) {
            LoadMoreState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
                Text(
                    text = stringResource(Res.string.common_loading),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            LoadMoreState.Error -> {
                AppDivider(Modifier.weight(1f))
                Text(
                    text = stringResource(Res.string.common_load_error_retry),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable(enabled = onRetry != null) { onRetry?.invoke() },
                )
                AppDivider(Modifier.weight(1f))
            }
            LoadMoreState.NoMore -> {
                val dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                AppDivider(Modifier.weight(1f), color = dividerColor)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(4.dp)
                        .background(dividerColor, CircleShape),
                )
                AppDivider(Modifier.weight(1f), color = dividerColor)
            }
            LoadMoreState.PullToLoad -> {
                AppDivider(Modifier.weight(1f))
                Text(
                    text = stringResource(Res.string.cs_load_more),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppDivider(Modifier.weight(1f))
            }
            LoadMoreState.Success -> {
                AppDivider(Modifier.weight(1f))
                Text(
                    text = stringResource(Res.string.common_load_success),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppDivider(Modifier.weight(1f))
            }
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
