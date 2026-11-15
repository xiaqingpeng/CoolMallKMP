package com.joker.coolmall.feature.feedback

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.core.model.entity.DictItem
import com.joker.coolmall.core.platform.PickedImage
import com.joker.coolmall.core.platform.rememberPlatformImagePicker
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_submit
import myapplication.shared.generated.resources.feedback_add_image
import myapplication.shared.generated.resources.feedback_contact
import myapplication.shared.generated.resources.feedback_contact_hint
import myapplication.shared.generated.resources.feedback_contact_section
import myapplication.shared.generated.resources.feedback_content
import myapplication.shared.generated.resources.feedback_content_hint
import myapplication.shared.generated.resources.feedback_images
import myapplication.shared.generated.resources.feedback_screenshot_section
import myapplication.shared.generated.resources.feedback_submit_title
import myapplication.shared.generated.resources.feedback_type
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.resolveUiMessageSuspend
import com.joker.coolmall.core.ui.resolveUiMessage
import myapplication.shared.generated.resources.feedback_type_suggestion
import myapplication.shared.generated.resources.feedback_type_experience
import myapplication.shared.generated.resources.feedback_type_other
import com.joker.coolmall.core.ui.theme.SpacePaddingMedium
import com.joker.coolmall.core.ui.theme.SpaceVerticalMedium

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedbackSubmitRoute(
    onBack: () -> Unit,
    viewModel: FeedbackSubmitViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val container = AppContainerProvider.instance
                FeedbackSubmitViewModel(
                    feedbackRepository = container.feedbackRepository,
                    commonRepository = container.commonRepository,
                    fileUploadRepository = container.fileUploadRepository,
                    authRepository = container.authRepository,
                )
            }
        }
    ),
) {
    val typesState by viewModel.typesState.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val contact by viewModel.contact.collectAsState()
    val content by viewModel.content.collectAsState()
    val pendingImages by viewModel.pendingImages.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pickSlots = viewModel.remainingImageSlots().coerceAtLeast(1)
    val launchPicker = rememberPlatformImagePicker(
        maxCount = pickSlots,
        onResult = viewModel::onImagesPicked,
    )

    LaunchedEffect(Unit) {
        viewModel.message.collect { message ->
            snackbarHostState.showSnackbar(resolveUiMessageSuspend(message))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.submitted.collect { onBack() }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.feedback_submit_title),
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (typesState is FeedbackTypesUiState.Success) {
                AppButton(
                    text = stringResource(Res.string.common_submit),
                    onClick = viewModel::submit,
                    enabled = content.isNotBlank() && selectedType != null && !isSubmitting,
                    loading = isSubmitting,
                    size = AppButtonSize.MEDIUM,
                    style = AppButtonStyle.GRADIENT,
                    fullWidth = true,
                    modifier = Modifier
                        .padding(16.dp)
                        .imePadding(),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(SpacePaddingMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium),
        ) {
            FeedbackSection(stringResource(Res.string.feedback_type)) {
                when (val state = typesState) {
                FeedbackTypesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }

                is FeedbackTypesUiState.Error -> {
                    Text(
                        text = resolveUiMessage(state.message),
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                is FeedbackTypesUiState.Success -> {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        state.types.forEach { item ->
                            TypeChip(
                                item = item,
                                selected = selectedType?.value == item.value &&
                                    selectedType?.name == item.name,
                                onClick = { viewModel.selectType(item) },
                            )
                        }
                    }
                }
            }
            }

            FeedbackSection(stringResource(Res.string.feedback_content)) {
                OutlinedTextField(
                    value = content,
                    onValueChange = viewModel::updateContent,
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text(stringResource(Res.string.feedback_content_hint)) },
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    enabled = !isSubmitting,
                )
            }

            FeedbackSection(stringResource(Res.string.feedback_contact_section)) {
                OutlinedTextField(
                    value = contact,
                    onValueChange = viewModel::updateContact,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(Res.string.feedback_contact_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    enabled = !isSubmitting,
                )
            }

            FeedbackSection(stringResource(Res.string.feedback_screenshot_section)) {
                FeedbackImageGrid(
                    pendingImages = pendingImages,
                    canAddMore = viewModel.remainingImageSlots() > 0 && !isSubmitting,
                    onAdd = launchPicker,
                    onRemove = viewModel::removePendingImage,
                )
            }
        }
    }
}

@Composable
private fun FeedbackSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(Modifier.padding(SpacePaddingMedium)) {
            TitleWithLine(title)
            content()
        }
    }
}

@Composable
private fun FeedbackImageGrid(
    pendingImages: List<PickedImage>,
    canAddMore: Boolean,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
) {
    val items = pendingImages.map { image -> image to false } +
        if (canAddMore) listOf(null to true) else emptyList()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium),
    ) {
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpaceVerticalMedium),
            ) {
                rowItems.forEach { (image, isAddButton) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    ) {
                        if (isAddButton) {
                            AddFeedbackImageButton(onClick = onAdd)
                        } else if (image != null) {
                            FeedbackImageItem(
                                image = image,
                                onRemove = { onRemove(image.uri) },
                            )
                        }
                    }
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FeedbackImageItem(
    image: PickedImage,
    onRemove: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        AsyncImage(
            model = image.uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.TopEnd)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp),
            )
        }
    }
}

@Composable
private fun AddFeedbackImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(4.dp),
            )
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(Res.string.feedback_add_image),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = stringResource(Res.string.feedback_add_image),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TitleWithLine(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun TypeChip(
    item: DictItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = when (item.name) {
                    "suggestion" -> stringResource(Res.string.feedback_type_suggestion)
                    "experience" -> stringResource(Res.string.feedback_type_experience)
                    "other" -> stringResource(Res.string.feedback_type_other)
                    else -> item.name.orEmpty().ifBlank {
                        stringResource(Res.string.feedback_type) + item.value.toString()
                    }
                },
            )
        },
    )
}
