package com.joker.coolmall.feature.main.view

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.model.entity.CategoryTree
import com.joker.coolmall.core.ui.empty.EmptyData
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.feature.main.component.CommonScaffold
import com.joker.coolmall.feature.main.viewmodel.CategoryUiState
import com.joker.coolmall.feature.main.viewmodel.CategoryViewModel
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.category_empty
import myapplication.shared.generated.resources.category_no_children
import myapplication.shared.generated.resources.category_title
import org.jetbrains.compose.resources.stringResource

/**
 * 分类 Tab 路由（对齐 CoolMallKotlin：CommonScaffold + 顶栏，自处理 statusBars）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryRoute(
    onCategoryClick: (Long, String) -> Unit = { _, _ -> },
    viewModel: CategoryViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CategoryViewModel(
                    goodsRepository = AppContainerProvider.instance.goodsRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedIndex by viewModel.selectedIndex.collectAsState()

    // MainRoute 已 exclude statusBars；本页用 CommonScaffold/TopAppBar 自行吃状态栏 inset
    CommonScaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.category_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        when (val state = uiState) {
            CategoryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is CategoryUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::loadCategories,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is CategoryUiState.Success -> {
                if (state.categories.isEmpty()) {
                    EmptyData(
                        message = stringResource(Res.string.category_empty),
                        onRetryClick = viewModel::loadCategories,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    )
                } else {
                    CategoryScreen(
                        categories = state.categories,
                        selectedIndex = selectedIndex.coerceIn(0, state.categories.lastIndex),
                        onCategorySelected = viewModel::selectCategory,
                        onCategoryClick = onCategoryClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryScreen(
    categories: List<CategoryTree>,
    selectedIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onCategoryClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = categories.getOrNull(selectedIndex) ?: categories.first()
    val children = selected.children

    Row(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .width(96.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        ) {
            itemsIndexed(
                items = categories,
                key = { index, item -> "${item.id}_$index" },
            ) { index, item ->
                val selectedItem = index == selectedIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (selectedItem) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            },
                        )
                        .clickable { onCategorySelected(index) }
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selectedItem) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(3.dp)
                                .height(18.dp)
                                .background(MaterialTheme.colorScheme.primary),
                        )
                    }
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedItem) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedItem) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        if (children.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.category_no_children),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(children, key = { it.id }) { child ->
                    CategoryChildItem(
                        category = child,
                        onClick = { onCategoryClick(child.id, child.name) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChildItem(
    category: CategoryTree,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
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
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
