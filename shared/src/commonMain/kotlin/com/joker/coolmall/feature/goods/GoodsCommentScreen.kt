package com.joker.coolmallkmp.feature.goods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.model.entity.Comment
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.common_load_more
import coolmallkmp.shared.generated.resources.common_retry
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
                GoodsCommentViewModel(goodsId, AppContainerProvider.instance.goodsRepository)
            }
        },
    ),
) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.goods_comment_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.common_back))
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, stringResource(Res.string.common_retry))
                    }
                },
            )
        },
    ) { padding ->
        when (val current = state) {
            GoodsCommentUiState.Loading -> GoodsCommentLoadingSkeleton(Modifier.padding(padding))
            is GoodsCommentUiState.Error -> EmptyUiMessage(
                message = current.message,
                onRetryClick = viewModel::refresh,
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            is GoodsCommentUiState.Success -> {
                if (current.comments.isEmpty()) {
                    Text(
                        stringResource(Res.string.goods_comment_empty),
                        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(current.comments, key = { it.id }) { comment ->
                            CommentCard(comment)
                        }
                        if (current.hasMore) {
                            item {
                                Text(
                                    stringResource(Res.string.common_load_more),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = viewModel::loadMore)
                                        .padding(16.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentCard(comment: Comment) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = comment.avatarUrl,
                    contentDescription = comment.nickName,
                    modifier = Modifier.size(36.dp),
                    contentScale = ContentScale.Crop,
                )
                Column(Modifier.padding(start = 10.dp)) {
                    Text(
                        comment.nickName ?: stringResource(Res.string.me_user_fallback),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        "★".repeat(comment.starCount.coerceIn(0, 5)),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Text(comment.content, style = MaterialTheme.typography.bodyMedium)
            comment.createTime?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
