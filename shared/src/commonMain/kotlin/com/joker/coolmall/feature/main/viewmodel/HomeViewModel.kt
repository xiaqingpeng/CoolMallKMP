package com.joker.coolmall.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.AuthRepository
import com.joker.coolmall.core.data.repository.CouponRepository
import com.joker.coolmall.core.data.repository.GoodsRepository
import com.joker.coolmall.core.data.repository.PageRepository
import com.joker.coolmall.core.model.entity.Coupon
import com.joker.coolmall.core.model.entity.Goods
import com.joker.coolmall.core.model.entity.Home
import com.joker.coolmall.core.model.request.GoodsSearchRequest
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Success : HomeUiState
    data class Error(val message: UiMessage) : HomeUiState
}

/**
 * 首页 VM：首页聚合数据 + 全部商品分页 + 优惠券领取弹层
 * （对齐 CoolMallKotlin HomeViewModel，去掉 Hilt/Navigator）
 */
class HomeViewModel(
    private val pageRepository: PageRepository,
    private val goodsRepository: GoodsRepository,
    private val couponRepository: CouponRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _pageData = MutableStateFlow(Home())
    val pageData: StateFlow<Home> = _pageData.asStateFlow()

    private val _listData = MutableStateFlow<List<Goods>>(emptyList())
    val listData: StateFlow<List<Goods>> = _listData.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _couponModalVisible = MutableStateFlow(false)
    val couponModalVisible: StateFlow<Boolean> = _couponModalVisible.asStateFlow()

    private val _toastMessage = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val toastMessage: SharedFlow<UiMessage> = _toastMessage.asSharedFlow()

    /** 领取优惠券时若未登录，由 UI 跳转登录 */
    private val _loginRequired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val loginRequired: SharedFlow<Unit> = _loginRequired.asSharedFlow()

    private var currentPage = 1
    private val pageSize = 20
    private val loadedGoods = mutableListOf<Goods>()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            if (!_isRefreshing.value) {
                _uiState.value = HomeUiState.Loading
            }
            runCatching {
                pageRepository.getHomeData()
            }.onSuccess { response ->
                _isRefreshing.value = false
                if (!response.isSucceeded) {
                    _uiState.value = HomeUiState.Error(
                        UiMessage.loadFailed(response.message, response.code),
                    )
                    return@onSuccess
                }
                val home = response.data ?: Home()
                _pageData.value = home
                // 用首页 goods 初始化全部商品列表
                loadedGoods.clear()
                loadedGoods.addAll(home.goods.orEmpty())
                currentPage = 1
                _listData.value = loadedGoods.toList()
                _hasMore.value = home.goods.orEmpty().isNotEmpty()
                _uiState.value = HomeUiState.Success
            }.onFailure { error ->
                _isRefreshing.value = false
                _uiState.value = HomeUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    fun onRefresh() {
        if (_isLoadingMore.value) return
        _isRefreshing.value = true
        currentPage = 1
        loadHomeData()
    }

    fun onLoadMore() {
        if (!_hasMore.value || _isLoadingMore.value || _isRefreshing.value) return
        if (_uiState.value !is HomeUiState.Success) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val nextPage = currentPage + 1
            runCatching {
                goodsRepository.getGoodsPage(
                    GoodsSearchRequest(
                        page = nextPage,
                        size = pageSize,
                    ),
                )
            }.onSuccess { response ->
                _isLoadingMore.value = false
                if (!response.isSucceeded) {
                    _toastMessage.tryEmit(
                        UiMessage.loadFailed(response.message, response.code),
                    )
                    return@onSuccess
                }
                val pageData = response.data
                val list = pageData?.list.orEmpty()
                if (list.isEmpty()) {
                    _hasMore.value = false
                    return@onSuccess
                }
                currentPage = nextPage
                loadedGoods.addAll(list)
                _listData.value = loadedGoods.toList()
                val total = pageData?.pagination?.total
                _hasMore.value = if (total != null) {
                    loadedGoods.size < total
                } else {
                    list.size >= pageSize
                }
            }.onFailure { error ->
                _isLoadingMore.value = false
                _toastMessage.tryEmit(UiMessage.network(error.message))
            }
        }
    }

    fun showCouponModal() {
        _couponModalVisible.value = true
    }

    fun hideCouponModal() {
        _couponModalVisible.value = false
    }

    fun receiveCoupon(coupon: Coupon) {
        if (!authRepository.isLoggedIn.value) {
            hideCouponModal()
            _loginRequired.tryEmit(Unit)
            return
        }
        viewModelScope.launch {
            runCatching {
                couponRepository.receiveCoupon(coupon.id)
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    _toastMessage.tryEmit(
                        response.data?.takeIf { it.isNotBlank() }?.let { UiMessage.Raw(it) }
                            ?: UiMessage.Code(MessageCode.CouponReceiveSuccess),
                    )
                    hideCouponModal()
                } else {
                    _toastMessage.tryEmit(
                        UiMessage.loadFailed(response.message, response.code),
                    )
                }
            }.onFailure { error ->
                _toastMessage.tryEmit(UiMessage.network(error.message))
            }
        }
    }
}
