package com.joker.coolmallkmp.feature.main.view.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.model.entity.CategoryTree
import com.joker.coolmallkmp.core.ui.empty.EmptyData
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.feature.main.component.CenterTopAppBar
import com.joker.coolmallkmp.feature.main.component.CommonScaffold
import com.joker.coolmallkmp.feature.main.view.skeleton.CategoryLoadingSkeleton
import com.joker.coolmallkmp.feature.main.viewmodel.CategoryUiState
import com.joker.coolmallkmp.feature.main.viewmodel.CategoryViewModel
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.category_empty
import coolmallkmp.shared.generated.resources.category_no_children
import coolmallkmp.shared.generated.resources.category_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Suppress("ParamsComparedByRef")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryRoute(
    onCategoryClick: (Long, String) -> Unit = { _, _ -> },
    viewModel: CategoryViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CategoryViewModel(AppContainerProvider.instance.goodsRepository)
            }
        },
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedIndex by viewModel.selectedIndex.collectAsState()

    CommonScaffold(
        topBar = {
            CenterTopAppBar(title = stringResource(Res.string.category_title))
        },
    ) { padding ->
        when (val state = uiState) {
            CategoryUiState.Loading -> CategoryLoadingSkeleton()

            is CategoryUiState.Error -> EmptyUiMessage(
                message = state.message,
                onRetryClick = viewModel::loadCategories,
                modifier = Modifier.fillMaxSize().padding(padding),
            )

            is CategoryUiState.Success -> if (state.categories.isEmpty()) {
                EmptyData(
                    message = stringResource(Res.string.category_empty),
                    onRetryClick = viewModel::loadCategories,
                    modifier = Modifier.fillMaxSize().padding(padding),
                )
            } else {
                CategoryContent(
                    categories = state.categories,
                    selectedIndex = selectedIndex.coerceIn(0, state.categories.lastIndex),
                    onCategorySelected = viewModel::selectCategory,
                    onCategoryClick = onCategoryClick,
                    modifier = Modifier.fillMaxSize().padding(padding),
                )
            }
        }
    }
}

@Suppress("FrequentlyChangingValue")
@Composable
private fun CategoryContent(
    categories: List<CategoryTree>,
    selectedIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onCategoryClick: (Long, String) -> Unit,
    modifier: Modifier,
) {
    val rightListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var syncingFromLeft by remember { mutableStateOf(false) }

    LaunchedEffect(rightListState.firstVisibleItemIndex) {
        if (!syncingFromLeft) {
            onCategorySelected(rightListState.firstVisibleItemIndex.coerceIn(0, categories.lastIndex))
        }
    }
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != rightListState.firstVisibleItemIndex) {
            syncingFromLeft = true
            rightListState.animateScrollToItem(selectedIndex)
            syncingFromLeft = false
        }
    }

    Row(modifier) {
        LazyColumn(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background),
        ) {
            itemsIndexed(categories, key = { index, item -> "${item.id}_$index" }) { index, category ->
                CategoryMenuItem(
                    name = category.name,
                    selected = index == selectedIndex,
                    first = index == 0,
                    previous = index == selectedIndex - 1,
                    next = index == selectedIndex + 1,
                    onClick = {
                        onCategorySelected(index)
                        scope.launch {
                            syncingFromLeft = true
                            rightListState.animateScrollToItem(index)
                            syncingFromLeft = false
                        }
                    },
                )
            }
            item { Spacer(Modifier.height(50.dp)) }
        }

        LazyColumn(
            state = rightListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(categories, key = { it.id }) { category ->
                Column(
                    Modifier.fillMaxWidth().padding(bottom = 8.dp),
                ) {
                    CategoryTitle(category.name)
                    Spacer(Modifier.height(8.dp))
                    category.children.chunked(3).forEach { row ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            row.forEach { child ->
                                CategoryChildItem(
                                    category = child,
                                    onClick = { onCategoryClick(child.id, child.name) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                    if (category.children.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.category_no_children),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTitle(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.width(3.dp).height(16.dp).background(MaterialTheme.colorScheme.primary),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun CategoryMenuItem(
    name: String,
    selected: Boolean,
    first: Boolean,
    previous: Boolean,
    next: Boolean,
    onClick: () -> Unit,
) {
    val shape = when {
        selected -> RoundedCornerShape(0.dp)
        first || next -> RoundedCornerShape(topEnd = 16.dp)
        previous -> RoundedCornerShape(bottomEnd = 16.dp)
        else -> RoundedCornerShape(0.dp)
    }
    val textColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "categoryMenuTextColor",
    )
    val indicatorWidth by animateDpAsState(
        if (selected) 3.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "categoryMenuIndicator",
    )

    Box(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                if (selected) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.background,
                shape,
            )
            .clickable(enabled = !selected, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (indicatorWidth > 0.dp) {
            Box(
                Modifier.width(indicatorWidth).height(24.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.CenterStart),
            )
        }
        Text(
            text = name,
            color = textColor,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        )
    }
}

@Composable
private fun CategoryChildItem(
    category: CategoryTree,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = category.pic,
            contentDescription = category.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
