package com.joker.coolmall.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.AuthRepository
import com.joker.coolmall.core.data.repository.FootprintRepository
import com.joker.coolmall.core.data.repository.OrderRepository
import com.joker.coolmall.core.data.repository.UserRepository
import com.joker.coolmall.core.model.entity.Footprint
import com.joker.coolmall.core.model.entity.OrderCount
import com.joker.coolmall.core.model.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 「我的」页：用户资料 + 订单角标 + 最近足迹（对齐 CoolMallKotlin MeViewModel）
 */
class MeViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    footprintRepository: FootprintRepository,
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
    val user: StateFlow<User?> = userRepository.user

    val recentFootprints: StateFlow<List<Footprint>> =
        footprintRepository.getRecentFootprints(8)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    private val _orderCount = MutableStateFlow(OrderCount())
    val orderCount: StateFlow<OrderCount> = _orderCount.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    runCatching { userRepository.refreshPersonInfo() }
                    refreshOrderCount()
                } else {
                    _orderCount.value = OrderCount()
                }
            }
        }
    }

    /** 页面可见时刷新订单角标与资料 */
    fun onResume() {
        if (!authRepository.isLoggedIn.value) {
            _orderCount.value = OrderCount()
            return
        }
        viewModelScope.launch {
            runCatching { userRepository.refreshPersonInfo() }
            refreshOrderCount()
        }
    }

    fun refreshOrderCount() {
        if (!authRepository.isLoggedIn.value) {
            _orderCount.value = OrderCount()
            return
        }
        viewModelScope.launch {
            runCatching { orderRepository.getUserOrderCount() }
                .onSuccess { response ->
                    if (response.isSucceeded && response.data != null) {
                        _orderCount.value = response.data
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _orderCount.value = OrderCount()
        }
    }
}