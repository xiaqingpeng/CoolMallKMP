package com.joker.coolmall.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.CartRepository
import com.joker.coolmall.core.model.entity.Cart
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 购物车 VM（对齐 CoolMallKotlin CartViewModel，去掉 Hilt）
 */
class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {

    val cartItems: StateFlow<List<Cart>> = cartRepository.cartItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val isEmpty: StateFlow<Boolean> = cartItems
        .map { it.isEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    val totalAmount: StateFlow<Int> = cartItems
        .map { carts ->
            carts.sumOf { cart -> cart.spec.sumOf { spec -> spec.price * spec.count } }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    val totalCount: StateFlow<Int> = cartItems
        .map { carts -> carts.sumOf { it.totalCount } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    fun increase(goodsId: Long, specId: Long, currentCount: Int) {
        viewModelScope.launch {
            cartRepository.updateSpecCount(goodsId, specId, currentCount + 1)
        }
    }

    fun decrease(goodsId: Long, specId: Long, currentCount: Int) {
        viewModelScope.launch {
            val next = currentCount - 1
            if (next <= 0) {
                cartRepository.removeSpec(goodsId, specId)
            } else {
                cartRepository.updateSpecCount(goodsId, specId, next)
            }
        }
    }

    fun remove(goodsId: Long, specId: Long) {
        viewModelScope.launch {
            cartRepository.removeSpec(goodsId, specId)
        }
    }

    fun clear() {
        viewModelScope.launch {
            cartRepository.clear()
        }
    }
}
