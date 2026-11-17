package com.joker.coolmallkmp.feature.market.view.screen
import com.joker.coolmallkmp.feature.market.viewmodel.CouponListViewModel
import com.joker.coolmallkmp.feature.market.view.skeleton.CouponListLoadingSkeleton

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.ui.component.CouponCard
import com.joker.coolmallkmp.core.ui.component.CouponCardMode
import com.joker.coolmallkmp.core.ui.component.RefreshLayout
import com.joker.coolmallkmp.core.ui.empty.EmptyData
import com.joker.coolmallkmp.core.ui.empty.EmptyUiMessage
import com.joker.coolmallkmp.core.common.base.state.BaseNetWorkListUiState
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.coupon_empty
import coolmallkmp.shared.generated.resources.coupon_list_title
import org.jetbrains.compose.resources.stringResource

@Suppress("ParamsComparedByRef")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponListRoute(
    onBack: () -> Unit,
    onUseCoupon: (Long) -> Unit,
    viewModel: CouponListViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CouponListViewModel(AppContainerProvider.instance.couponRepository)
            }
        }
    ),
) {
    val uiState by viewModel.networkUiState.collectAsState()
    val coupons by viewModel.listData.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val loadMoreState by viewModel.loadMoreState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    com.joker.coolmallkmp.core.ui.component.AppScaffold(
        title = stringResource(Res.string.coupon_list_title),
        onBack = onBack,
    ) { padding ->
        when (uiState) {
            BaseNetWorkListUiState.Loading -> {
                CouponListLoadingSkeleton(Modifier.padding(padding))
            }
            BaseNetWorkListUiState.Error -> {
                EmptyUiMessage(
                    message = errorMessage
                        ?: com.joker.coolmallkmp.core.ui.UiMessage.network(null),
                    onRetryClick = viewModel::retryRequest,
                    modifier = Modifier.fillMaxSize().padding(padding),
                )
            }
            BaseNetWorkListUiState.Empty -> {
                EmptyData(
                    message = stringResource(Res.string.coupon_empty),
                    onRetryClick = viewModel::retryRequest,
                    modifier = Modifier.fillMaxSize().padding(padding),
                )
            }
            BaseNetWorkListUiState.Success -> {
                RefreshLayout(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    isRefreshing = isRefreshing,
                    loadMoreState = loadMoreState,
                    onRefresh = viewModel::refresh,
                    onLoadMore = viewModel::loadMore,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                ) {
                    items(coupons.size, key = { coupons[it].id }) { index ->
                        CouponCard(
                            coupon = coupons[index],
                            mode = CouponCardMode.VIEW,
                            onActionClick = { onUseCoupon(coupons[index].id) },
                        )
                    }
                }
            }
        }
    }
}
