package com.joker.coolmall.feature.feedback

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                        .fillMaxWidth()
                        .height(128.dp),
                    placeholder = { Text(stringResource(Res.string.feedback_content_hint)) },
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
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
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
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        pendingImages.forEach { image ->
            Box {
                AsyncImage(
                    model = image.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                IconButton(
                    onClick = { onRemove(image.uri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        if (canAddMore) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clickable(onClick = onAdd),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(Res.string.feedback_add_image),
                    )
                    Text(
                        text = stringResource(Res.string.feedback_add_image),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
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
