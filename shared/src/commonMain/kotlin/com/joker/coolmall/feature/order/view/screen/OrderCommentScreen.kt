package com.joker.coolmallkmp.feature.order.view.screen
import com.joker.coolmallkmp.feature.order.viewmodel.OrderCommentViewModel
import com.joker.coolmallkmp.feature.order.viewmodel.*

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.joker.coolmallkmp.core.ui.component.AppButton
import com.joker.coolmallkmp.core.ui.component.AppButtonSize
import com.joker.coolmallkmp.core.ui.component.AppButtonStyle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.feature.main.component.CenterTopAppBar
import com.joker.coolmallkmp.core.model.entity.OrderGoods
import com.joker.coolmallkmp.core.platform.PickedImage
import com.joker.coolmallkmp.core.platform.rememberPlatformImagePicker
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.common_goods
import coolmallkmp.shared.generated.resources.common_retry
import coolmallkmp.shared.generated.resources.order_comment_add
import coolmallkmp.shared.generated.resources.order_comment_manual_url_hint
import coolmallkmp.shared.generated.resources.order_comment_pending_upload
import coolmallkmp.shared.generated.resources.order_comment_pic
import coolmallkmp.shared.generated.resources.order_comment_pics_hint
import coolmallkmp.shared.generated.resources.order_comment_pics_title
import coolmallkmp.shared.generated.resources.order_comment_placeholder
import coolmallkmp.shared.generated.resources.order_comment_rating_1
import coolmallkmp.shared.generated.resources.order_comment_rating_2
import coolmallkmp.shared.generated.resources.order_comment_rating_3
import coolmallkmp.shared.generated.resources.order_comment_rating_4
import coolmallkmp.shared.generated.resources.order_comment_rating_5
import coolmallkmp.shared.generated.resources.order_comment_rating_cd
import coolmallkmp.shared.generated.resources.order_comment_rating_none
import coolmallkmp.shared.generated.resources.order_comment_select_goods
import coolmallkmp.shared.generated.resources.order_comment_submit
import coolmallkmp.shared.generated.resources.order_comment_title
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCommentRoute(
    orderId: Long,
    goodsId: Long = 0,
    onBack: () -> Unit,
    viewModel: OrderCommentViewModel = viewModel(
        key = "order_comment_${orderId}_$goodsId",
        factory = viewModelFactory {
            initializer {
                val container = AppContainerProvider.instance
                OrderCommentViewModel(
                    orderId = orderId,
                    initialGoodsId = goodsId,
                    orderRepository = container.orderRepository,
                    goodsRepository = container.goodsRepository,
                    fileUploadRepository = container.fileUploadRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val content by viewModel.content.collectAsState()
    val picUrls by viewModel.picUrls.collectAsState()
    val pendingImages by viewModel.pendingImages.collectAsState()
    val manualUrlDraft by viewModel.manualUrlDraft.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pickSlots = (9 - picUrls.size - pendingImages.size).coerceAtLeast(1)
    val launchPicker = rememberPlatformImagePicker(
        maxCount = pickSlots,
        onResult = viewModel::onImagesPicked,
    )

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }
    LaunchedEffect(Unit) {
        viewModel.submitted.collect { onBack() }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.order_comment_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val canSubmit = uiState is OrderCommentUiState.Success &&
                content.isNotBlank() &&
                rating > 0 &&
                !isSubmitting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                AppButton(
                    text = stringResource(Res.string.order_comment_submit),
                    onClick = viewModel::submit,
                    enabled = canSubmit,
                    loading = isSubmitting,
                    size = AppButtonSize.MEDIUM,
                    style = AppButtonStyle.GRADIENT,
                    fullWidth = true,
                )
            }
        },
    ) { padding ->
        when (val state = uiState) {
            OrderCommentUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is OrderCommentUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::load,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is OrderCommentUiState.Success -> {
                OrderCommentContent(
                    goodsList = state.goodsList,
                    selectedGoodsId = state.selectedGoodsId,
                    rating = rating,
                    content = content,
                    picUrls = picUrls,
                    pendingImages = pendingImages,
                    manualUrlDraft = manualUrlDraft,
                    onSelectGoods = viewModel::selectGoods,
                    onRatingChange = viewModel::updateRating,
                    onContentChange = viewModel::updateContent,
                    onPickImages = {
                        if (viewModel.remainingPicSlots() <= 0) return@OrderCommentContent
                        launchPicker()
                    },
                    onManualUrlChange = viewModel::updateManualUrlDraft,
                    onAddManualUrl = viewModel::addManualPicUrl,
                    onRemovePicUrl = viewModel::removePicUrl,
                    onRemovePending = viewModel::removePendingImage,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun OrderCommentContent(
    goodsList: List<OrderGoods>,
    selectedGoodsId: Long,
    rating: Int,
    content: String,
    picUrls: List<String>,
    pendingImages: List<PickedImage>,
    manualUrlDraft: String,
    onSelectGoods: (Long) -> Unit,
    onRatingChange: (Int) -> Unit,
    onContentChange: (String) -> Unit,
    onPickImages: () -> Unit,
    onManualUrlChange: (String) -> Unit,
    onAddManualUrl: () -> Unit,
    onRemovePicUrl: (String) -> Unit,
    onRemovePending: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (goodsList.size > 1) {
            Text(
                text = stringResource(Res.string.order_comment_select_goods),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                goodsList.forEach { goods ->
                    GoodsPickCard(
                        goods = goods,
                        selected = goods.goodsId == selectedGoodsId,
                        onClick = { onSelectGoods(goods.goodsId) },
                    )
                }
            }
        } else {
            goodsList.firstOrNull()?.let { goods ->
                GoodsSummaryCard(goods = goods)
            }
        }

        StarRating(
            rating = rating,
            onRatingChange = onRatingChange,
        )
        Text(
            text = ratingLabel(rating),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            placeholder = { Text(stringResource(Res.string.order_comment_placeholder)) },
            minLines = 5,
        )

        CommentPicsSection(
            picUrls = picUrls,
            pendingImages = pendingImages,
            manualUrlDraft = manualUrlDraft,
            onPickImages = onPickImages,
            onManualUrlChange = onManualUrlChange,
            onAddManualUrl = onAddManualUrl,
            onRemovePicUrl = onRemovePicUrl,
            onRemovePending = onRemovePending,
        )
    }
}

@Composable
private fun CommentPicsSection(
    picUrls: List<String>,
    pendingImages: List<PickedImage>,
    manualUrlDraft: String,
    onPickImages: () -> Unit,
    onManualUrlChange: (String) -> Unit,
    onAddManualUrl: () -> Unit,
    onRemovePicUrl: (String) -> Unit,
    onRemovePending: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(Res.string.order_comment_pics_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            picUrls.forEach { url ->
                Box {
                    AsyncImage(
                        model = url,
                        contentDescription = stringResource(Res.string.order_comment_pic),
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        text = "×",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onRemovePicUrl(url) }
                            .background(
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f),
                                RoundedCornerShape(bottomStart = 6.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            pendingImages.forEach { image ->
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onRemovePending(image.uri) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.order_comment_pending_upload),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (picUrls.size + pendingImages.size < 9) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(8.dp),
                        )
                        .clickable(onClick = onPickImages),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        OutlinedTextField(
            value = manualUrlDraft,
            onValueChange = onManualUrlChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(Res.string.order_comment_manual_url_hint)) },
            singleLine = true,
            trailingIcon = {
                Text(
                    text = stringResource(Res.string.order_comment_add),
                    modifier = Modifier
                        .clickable(onClick = onAddManualUrl)
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            },
        )
        Text(
            text = stringResource(Res.string.order_comment_pics_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun GoodsSummaryCard(goods: OrderGoods) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = goods.displayPic,
                contentDescription = goods.goodsName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goods.goodsName.ifBlank { stringResource(Res.string.common_goods) },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (goods.specName.isNotBlank()) {
                    Text(
                        text = goods.specName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun GoodsPickCard(
    goods: OrderGoods,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    Column(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = goods.displayPic,
            contentDescription = goods.goodsName,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = goods.goodsName.ifBlank { stringResource(Res.string.common_goods) },
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StarRating(
    rating: Int,
    onRatingChange: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        (1..5).forEach { star ->
            IconButton(
                onClick = { onRatingChange(star) },
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = if (star <= rating) {
                        Icons.Filled.Star
                    } else {
                        Icons.Outlined.StarOutline
                    },
                    contentDescription = stringResource(Res.string.order_comment_rating_cd, star),
                    tint = if (star <= rating) {
                        Color(0xFFFFB300)
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

@Composable
private fun ratingLabel(rating: Int): String = when (rating) {
    1 -> stringResource(Res.string.order_comment_rating_1)
    2 -> stringResource(Res.string.order_comment_rating_2)
    3 -> stringResource(Res.string.order_comment_rating_3)
    4 -> stringResource(Res.string.order_comment_rating_4)
    5 -> stringResource(Res.string.order_comment_rating_5)
    else -> stringResource(Res.string.order_comment_rating_none)
}
