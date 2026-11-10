package com.joker.coolmall.feature.cs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.model.entity.CsMsg
import com.joker.coolmall.core.platform.rememberPlatformImagePicker
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.cs_chat_title
import myapplication.shared.generated.resources.cs_connected
import myapplication.shared.generated.resources.cs_connecting
import myapplication.shared.generated.resources.cs_disconnected
import myapplication.shared.generated.resources.cs_input_hint
import myapplication.shared.generated.resources.cs_load_more
import myapplication.shared.generated.resources.cs_retry
import myapplication.shared.generated.resources.cs_role_agent
import myapplication.shared.generated.resources.cs_role_me
import myapplication.shared.generated.resources.cs_send
import myapplication.shared.generated.resources.cs_send_image
import myapplication.shared.generated.resources.cs_uploading_image
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.resolveUiMessageSuspend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoute(
    onBack: () -> Unit,
    viewModel: ChatViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val container = AppContainerProvider.instance
                ChatViewModel(
                    customerServiceRepository = container.customerServiceRepository,
                    fileUploadRepository = container.fileUploadRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isLoadingHistory by viewModel.isLoadingHistory.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val isSendingImage by viewModel.isSendingImage.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val launchImagePicker = rememberPlatformImagePicker(
        maxCount = 1,
        onResult = viewModel::onImagesPicked,
    )

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(Res.string.cs_chat_title))
                        Text(
                            text = when (val cs = connectionState) {
                                WebSocketConnectionState.Connected ->
                                    stringResource(Res.string.cs_connected)
                                WebSocketConnectionState.Connecting ->
                                    stringResource(Res.string.cs_connecting)
                                is WebSocketConnectionState.Error -> cs.message
                                WebSocketConnectionState.Disconnected ->
                                    stringResource(Res.string.cs_disconnected)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState is ChatUiState.Success) {
                ChatInputBar(
                    text = inputText,
                    onTextChange = viewModel::updateInputText,
                    onSend = viewModel::sendMessage,
                    onPickImage = launchImagePicker,
                    isSendingImage = isSendingImage,
                )
            }
        },
    ) { padding ->
        when (val state = uiState) {
            ChatUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            is ChatUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::retry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            ChatUiState.Success -> {
                ChatMessageList(
                    messages = messages,
                    isLoadingHistory = isLoadingHistory,
                    hasMore = hasMore,
                    onLoadMore = viewModel::loadMore,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun ChatMessageList(
    messages: List<CsMsg>,
    isLoadingHistory: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    // reverseLayout：最新在底部视觉上靠下；数据仍是新消息在 index 0
    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            last to info.totalItemsCount
        }.collect { (last, total) ->
            if (hasMore && !isLoadingHistory && total > 0 && last >= total - 2) {
                onLoadMore()
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(messages, key = { it.id }) { msg ->
            ChatBubble(msg)
        }
        if (isLoadingHistory || hasMore) {
            item(key = "footer") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoadingHistory) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else if (hasMore) {
                        Text(
                            text = stringResource(Res.string.cs_load_more),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: CsMsg) {
    val isMe = msg.type == 0
    val bubbleColor = if (isMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isMe) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val shape = if (isMe) {
        RoundedCornerShape(18.dp, 4.dp, 18.dp, 18.dp)
    } else {
        RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
    ) {
        if (!isMe) {
            val avatar = msg.adminUserHeadImg.ifBlank { msg.avatarUrl }
            if (avatar.isNotBlank()) {
                AsyncImage(
                    model = avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .padding(end = 0.dp),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp),
        ) {
            val name = if (isMe) {
                msg.nickName.ifBlank { stringResource(Res.string.cs_role_me) }
            } else {
                msg.adminUserName.ifBlank { stringResource(Res.string.cs_role_agent) }
            }
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                color = bubbleColor,
                shape = shape,
            ) {
                when (msg.content.type.uppercase()) {
                    "IMAGE" -> {
                        AsyncImage(
                            model = msg.content.data,
                            contentDescription = null,
                            modifier = Modifier
                                .size(160.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    else -> {
                        Text(
                            text = msg.content.data.ifBlank { " " },
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onPickImage: () -> Unit,
    isSendingImage: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        if (isSendingImage) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Text(
                    text = stringResource(Res.string.cs_uploading_image),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(
                onClick = onPickImage,
                enabled = !isSendingImage,
            ) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = stringResource(Res.string.cs_send_image),
                    tint = if (!isSendingImage) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(Res.string.cs_input_hint)) },
                maxLines = 4,
                enabled = !isSendingImage,
            )
            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank() && !isSendingImage,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(Res.string.cs_send),
                    tint = if (text.isNotBlank() && !isSendingImage) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}
