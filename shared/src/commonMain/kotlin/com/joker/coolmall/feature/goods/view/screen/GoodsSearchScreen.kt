package com.joker.coolmallkmp.feature.goods.view.screen
import com.joker.coolmallkmp.feature.goods.viewmodel.GoodsSearchViewModel
import com.joker.coolmallkmp.feature.goods.viewmodel.*

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.core.ui.component.SkeletonBlock
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.common_search
import coolmallkmp.shared.generated.resources.search_hint
import coolmallkmp.shared.generated.resources.search_history
import coolmallkmp.shared.generated.resources.search_history_clear
import coolmallkmp.shared.generated.resources.search_no_keywords
import coolmallkmp.shared.generated.resources.search_recommended
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GoodsSearchRoute(
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    viewModel: GoodsSearchViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val c = AppContainerProvider.instance
                GoodsSearchViewModel(
                    goodsRepository = c.goodsRepository,
                    searchHistoryRepository = c.searchHistoryRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()
    val history by viewModel.history.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current

    fun performSearch(raw: String = query) {
        viewModel.commitSearch(raw)?.let { keyword ->
            keyboard?.hide()
            onSearch(keyword)
        }
    }

    val canSearch = query.trim().isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(Res.string.common_search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                        BasicTextField(
                            value = query,
                            onValueChange = viewModel::updateQuery,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { performSearch() }),
                            decorationBox = { innerTextField ->
                                if (query.isBlank()) {
                                    Text(
                                        stringResource(Res.string.search_hint),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                innerTextField()
                            },
                        )
                        if (query.isNotBlank()) {
                            IconButton(
                                onClick = { viewModel.updateQuery("") },
                                modifier = Modifier.size(40.dp),
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = stringResource(Res.string.search_history_clear),
                                modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                        Text(
                            text = stringResource(Res.string.common_search),
                            color = Color.White,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable(enabled = canSearch, onClick = { performSearch() })
                                .height(34.dp)
                                .padding(horizontal = 24.dp),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
            Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (history.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.search_history),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = viewModel::clearHistory) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.search_history_clear))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    history.forEach { item ->
                        SearchKeywordTag(
                            text = item,
                            onClick = {
                                viewModel.updateQuery(item)
                                performSearch(item)
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.search_recommended),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            when (val state = uiState) {
                GoodsSearchUiState.Loading -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(3) { SkeletonBlock(Modifier.fillMaxWidth().height(40.dp), CircleShape) }
                    }
                }

                is GoodsSearchUiState.Error -> {
                    EmptyUiMessage(
                        message = state.message,
                        onRetryClick = viewModel::loadKeywords,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                    )
                }

                is GoodsSearchUiState.Success -> {
                    if (state.keywords.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.search_no_keywords),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            state.keywords.forEach { keyword ->
                                SearchKeywordTag(
                                    text = keyword.name,
                                    onClick = {
                                        viewModel.updateQuery(keyword.name)
                                        performSearch(keyword.name)
                                    },
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
private fun SearchKeywordTag(
    text: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}
