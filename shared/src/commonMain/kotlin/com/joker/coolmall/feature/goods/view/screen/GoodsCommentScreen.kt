package com.joker.coolmallkmp.feature.goods.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmallkmp.core.common.base.state.BaseNetWorkListUiState
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.model.entity.Comment
import com.joker.coolmallkmp.core.ui.component.AppScaffold
import com.joker.coolmallkmp.core.ui.component.RefreshLayout
import com.joker.coolmallkmp.core.ui.empty.EmptyData
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.feature.goods.view.skeleton.GoodsCommentLoadingSkeleton
import com.joker.coolmallkmp.feature.goods.viewmodel.GoodsCommentViewModel
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.goods_comment_empty
import coolmallkmp.shared.generated.resources.goods_comment_title
import coolmallkmp.shared.generated.resources.me_user_fallback
import org.jetbrains.compose.resources.stringResource

@Composable
fun GoodsCommentRoute(
    goodsId: Long,
    onBack: () -> Unit,
    viewModel: GoodsCommentViewModel = viewModel(
        key = "goods_comments_$goodsId",
        factory = viewModelFactory {
            initializer {
                GoodsCommentViewModel(
                    goodsId = goodsId,
                    repository = AppContainerProvider.instance.goodsRepository,
                )
            }
        },
    ),
) {
    val state by viewModel.networkUiState.collectAsState()
    val comments by viewModel.listData.collectAsState()
    val refreshing by viewModel.isRefreshing.collectAsState()
    val loadMoreState by viewModel.loadMoreState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    AppScaffold(
        title = stringResource(Res.string.goods_comment_title),
        onBack = onBack,
    ) { padding ->
        when (state) {
            BaseNetWorkListUiState.Loading ->
                GoodsCommentLoadingSkeleton(Modifier.padding(padding))
            BaseNetWorkListUiState.Error ->
                EmptyUiMessage(
                    message = errorMessage
                        ?: com.joker.coolmallkmp.core.ui.UiMessage.network(null),
                    onRetryClick = viewModel::retryRequest,
                    modifier = Modifier.fillMaxSize().padding(padding),
                )
            BaseNetWorkListUiState.Empty ->
                EmptyData(
                    message = stringResource(Res.string.goods_comment_empty),
                    onRetryClick = viewModel::retryRequest,
                    modifier = Modifier.fillMaxSize().padding(padding),
                )
            BaseNetWorkListUiState.Success ->
                RefreshLayout(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    isRefreshing = refreshing,
                    loadMoreState = loadMoreState,
                    onRefresh = viewModel::refresh,
                    onLoadMore = viewModel::loadMore,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(comments.size, key = { comments[it].id }) { index ->
                        CommentCard(comments[index])
                    }
                }
        }
    }
}

@Composable
private fun CommentCard(comment: Comment) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = comment.avatarUrl,
                contentDescription = comment.nickName,
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = comment.nickName
                            ?: stringResource(Res.string.me_user_fallback),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    comment.createTime?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.size(4.dp))
                Text(
                    text = "★".repeat(comment.starCount.coerceIn(0, 5)),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (comment.content.isNotBlank()) {
                    Spacer(Modifier.size(12.dp))
                    Text(
                        text = comment.content,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                val images = comment.pics.orEmpty().take(9)
                if (images.isNotEmpty()) {
                    Spacer(Modifier.size(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        images.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                row.forEach { image ->
                                    AsyncImage(
                                        model = image,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                                repeat(3 - row.size) {
                                    Spacer(Modifier.weight(1f).aspectRatio(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
