package com.joker.coolmall.feature.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.model.entity.Feedback
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_retry
import myapplication.shared.generated.resources.feedback_list_title
import myapplication.shared.generated.resources.feedback_submit_title
import org.jetbrains.compose.resources.stringResource
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.empty.EmptyData
import myapplication.shared.generated.resources.feedback_empty
import myapplication.shared.generated.resources.feedback_empty_hint
import myapplication.shared.generated.resources.feedback_no_content
import myapplication.shared.generated.resources.feedback_reply
import myapplication.shared.generated.resources.feedback_status_done
import myapplication.shared.generated.resources.feedback_status_pending

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackListRoute(
    onBack: () -> Unit,
    onSubmitClick: () -> Unit,
    viewModel: FeedbackListViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                FeedbackListViewModel(AppContainerProvider.instance.feedbackRepository)
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.feedback_list_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSubmitClick) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(Res.string.feedback_submit_title),
                )
            }
        },
    ) { padding ->
        when (val state = uiState) {
            FeedbackListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.list, key = { it.id }) { item ->
                            FeedbackCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackCard(feedback: Feedback) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = feedback.content.ifBlank { stringResource(Res.string.feedback_no_content) },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusLabel(feedback.status) +
                    (feedback.createTime?.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            feedback.remark?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.feedback_reply, it),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun statusLabel(status: Int): String = when (status) {
    1 -> stringResource(Res.string.feedback_status_done)
    else -> stringResource(Res.string.feedback_status_pending)
}
