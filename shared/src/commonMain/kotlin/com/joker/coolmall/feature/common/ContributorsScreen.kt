package com.joker.coolmall.feature.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import com.joker.coolmall.core.model.entity.UserContributor
import com.joker.coolmall.core.platform.openExternalUrl
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.core.ui.component.ListCardSkeleton
import com.joker.coolmall.core.ui.resolveUiMessage
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_load_more
import myapplication.shared.generated.resources.common_retry
import myapplication.shared.generated.resources.contributors_empty
import myapplication.shared.generated.resources.contributors_signature_empty
import myapplication.shared.generated.resources.contributors_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ContributorsRoute(
    onBack: () -> Unit,
    viewModel: ContributorsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ContributorsViewModel(AppContainerProvider.instance.userContributorRepository)
            }
        },
    ),
) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.contributors_title),
                showBackIcon = true,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(Res.string.common_retry),
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (val current = state) {
            ContributorsUiState.Loading -> ListCardSkeleton(Modifier.padding(padding))
            is ContributorsUiState.Error -> EmptyUiMessage(
                message = current.message,
                onRetryClick = viewModel::refresh,
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            is ContributorsUiState.Success -> {
                if (current.items.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.contributors_empty),
                        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(current.items, key = { it.id }) { contributor ->
                            ContributorRow(contributor)
                        }
                        if (current.hasMore) {
                            item {
                                Text(
                                    text = stringResource(Res.string.common_load_more),
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
private fun ContributorRow(contributor: UserContributor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                contributor.websiteUrl?.takeIf { it.isNotBlank() }?.let(::openExternalUrl)
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = contributor.avatarUrl,
            contentDescription = contributor.nickName,
            modifier = Modifier.size(48.dp).clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(contributor.nickName, style = MaterialTheme.typography.titleMedium)
            Text(
                contributor.signature ?: stringResource(Res.string.contributors_signature_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            contributor.tags.orEmpty().takeIf { it.isNotEmpty() }?.let {
                Text(
                    it.joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
