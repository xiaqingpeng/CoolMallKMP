package com.joker.coolmall.feature.feedback

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.model.entity.Feedback
import com.joker.coolmall.core.model.entity.DictItem
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import coil3.compose.AsyncImage
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_retry
import myapplication.shared.generated.resources.feedback_list_title
import myapplication.shared.generated.resources.feedback_submit_title
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.empty.EmptyData
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import myapplication.shared.generated.resources.feedback_empty
import myapplication.shared.generated.resources.feedback_empty_hint
import myapplication.shared.generated.resources.feedback_no_content
import myapplication.shared.generated.resources.feedback_reply
import myapplication.shared.generated.resources.feedback_status_done
import myapplication.shared.generated.resources.feedback_status_pending
import myapplication.shared.generated.resources.feedback_type_experience
import myapplication.shared.generated.resources.feedback_type_other
import myapplication.shared.generated.resources.feedback_type_suggestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackListRoute(
    onBack: () -> Unit,
    onSubmitClick: () -> Unit,
    viewModel: FeedbackListViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                FeedbackListViewModel(
                    feedbackRepository = AppContainerProvider.instance.feedbackRepository,
                    commonRepository = AppContainerProvider.instance.commonRepository,
                    authRepository = AppContainerProvider.instance.authRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val feedbackTypes by viewModel.feedbackTypes.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 提交页返回时列表页会重新进入 RESUMED，重新请求以展示刚提交的反馈。
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.feedback_list_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        bottomBar = {
            if (uiState is FeedbackListUiState.Success) {
                AppButton(
                    text = stringResource(Res.string.feedback_submit_title),
                    onClick = onSubmitClick,
                    size = AppButtonSize.MEDIUM,
                    style = AppButtonStyle.GRADIENT,
                    fullWidth = true,
                    modifier = Modifier.padding(16.dp),
                )
            }
        },
    ) { padding ->
        when (val state = uiState) {
            FeedbackListUiState.Loading -> {
                FeedbackListSkeleton(Modifier.padding(padding))
            }

            is FeedbackListUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::load,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is FeedbackListUiState.Success -> {
                if (state.list.isEmpty()) {
                    EmptyData(
                        message = stringResource(Res.string.feedback_empty),
                        subtitle = stringResource(Res.string.feedback_empty_hint),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    )
                } else {
                    val listState = rememberLazyListState()
                    val pullState = rememberPullToRefreshState()
                    LaunchedEffect(listState, state.list.size) {
                        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
                            .distinctUntilChanged()
                            .filter { it >= state.list.lastIndex - 2 }
                            .collectLatest { viewModel.loadMore() }
                    }
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = viewModel::refresh,
                        state = pullState,
                        modifier = Modifier.fillMaxSize().padding(padding),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.list, key = { it.id }) { item ->
                                FeedbackCard(item, feedbackTypes)
                            }
                            if (isLoadingMore) {
                                item {
                                    Box(
                                        Modifier.fillMaxWidth().padding(12.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "加载更多…",
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
}

@Composable
private fun FeedbackCard(feedback: Feedback, feedbackTypes: List<DictItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FeedbackTag(typeLabel(feedback.type, feedbackTypes), MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(8.dp))
                FeedbackTag(
                    statusLabel(feedback.status),
                    if (feedback.status == 1) Color(0xFF2E9B61) else Color(0xFFE18A28),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = feedback.createTime.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feedback.content.ifBlank { stringResource(Res.string.feedback_no_content) },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            feedback.images?.takeIf { it.isNotEmpty() }?.let { images ->
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    images.take(9).chunked(3).forEach { rowImages ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowImages.forEach { image ->
                                AsyncImage(
                                    model = image,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp)),
                                )
                            }
                            repeat(3 - rowImages.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            feedback.remark?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp),
                        )
                        .padding(12.dp),
                )
                {
                    Text(
                    text = stringResource(Res.string.feedback_reply),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun FeedbackTag(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun typeLabel(type: Int, types: List<DictItem>): String {
    val dictName = types.firstOrNull { it.value == type }?.name
    return when {
        dictName == "suggestion" -> stringResource(Res.string.feedback_type_suggestion)
        dictName == "experience" -> stringResource(Res.string.feedback_type_experience)
        dictName == "other" -> stringResource(Res.string.feedback_type_other)
        dictName != null -> dictName
        type == 0 -> "崩溃与错误"
        type == 1 -> "支付问题"
        type == 2 -> "体验不佳"
        type == 3 -> "功能建议"
        type == 4 -> "商品建议"
        type == 5 -> "活动建议"
        type == 6 -> "物流售后"
        else -> "其他"
    }
}

@Composable
private fun statusLabel(status: Int): String = when (status) {
    1 -> stringResource(Res.string.feedback_status_done)
    else -> stringResource(Res.string.feedback_status_pending)
}
