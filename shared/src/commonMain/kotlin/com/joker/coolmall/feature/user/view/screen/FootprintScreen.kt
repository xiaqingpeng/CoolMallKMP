package com.joker.coolmallkmp.feature.user.view.screen
import com.joker.coolmallkmp.feature.user.viewmodel.FootprintViewModel
import com.joker.coolmallkmp.feature.user.viewmodel.*

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.joker.coolmallkmp.core.model.entity.Footprint
import com.joker.coolmallkmp.core.ui.component.PriceTextSmall
import com.joker.coolmallkmp.core.ui.empty.EmptyData
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.cart_clear
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.common_delete
import coolmallkmp.shared.generated.resources.common_goods
import coolmallkmp.shared.generated.resources.footprint_empty
import coolmallkmp.shared.generated.resources.footprint_empty_hint
import coolmallkmp.shared.generated.resources.footprint_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FootprintRoute(
    onBack: () -> Unit,
    onGoodsClick: (Long) -> Unit,
    viewModel: FootprintViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                FootprintViewModel(AppContainerProvider.instance.footprintRepository)
            }
        }
    ),
) {
    val footprints by viewModel.footprints.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(Res.string.footprint_title),
                showBackIcon = true,
                onBackClick = onBack,
                actions = {
                    if (footprints.isNotEmpty()) {
                        TextButton(onClick = viewModel::clearAll) {
                            Text(stringResource(Res.string.cart_clear))
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (footprints.isEmpty()) {
            EmptyData(
                message = stringResource(Res.string.footprint_empty),
                subtitle = stringResource(Res.string.footprint_empty_hint),
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
                items(footprints, key = { it.goodsId }) { item ->
                    FootprintCard(
                        footprint = item,
                        onClick = { onGoodsClick(item.goodsId) },
                        onDelete = { viewModel.remove(item.goodsId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FootprintCard(
    footprint: Footprint,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = footprint.goodsMainPic,
                contentDescription = footprint.goodsName,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = footprint.goodsName.ifBlank { stringResource(Res.string.common_goods) },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                footprint.goodsSubTitle?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                PriceTextSmall(price = footprint.goodsPrice)
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(Res.string.common_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
