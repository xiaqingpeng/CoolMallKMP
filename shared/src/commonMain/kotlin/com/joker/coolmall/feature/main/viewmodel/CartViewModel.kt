package com.joker.coolmallkmp.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.CartRepository
import com.joker.coolmallkmp.core.model.entity.Cart
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 购物车 VM（对齐 CoolMallKotlin CartViewModel，去掉 Hilt）
 */
class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _selectedItems = MutableStateFlow<Map<Long, Set<Long>>>(emptyMap())
    val selectedItems: StateFlow<Map<Long, Set<Long>>> = _selectedItems.asStateFlow()

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

    val isAllSelected: StateFlow<Boolean> = combine(cartItems, selectedItems) { carts, selected ->
        val allSpecIds = carts.flatMap { cart -> cart.spec.map { cart.goodsId to it.id } }.toSet()
        allSpecIds.isNotEmpty() && allSpecIds.all { (goodsId, specId) ->
            selected[goodsId]?.contains(specId) == true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val selectedCount: StateFlow<Int> = selectedItems
        .map { selected -> selected.values.sumOf { it.size } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val selectedAmount: StateFlow<Int> = combine(cartItems, selectedItems) { carts, selected ->
        carts.sumOf { cart ->
            cart.spec.filter { selected[cart.goodsId]?.contains(it.id) == true }
                .sumOf { it.price * it.count }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        viewModelScope.launch {
            cartItems.collect { carts ->
                val valid = carts.associate { cart ->
                    cart.goodsId to cart.spec.map { it.id }.toSet()
                }
                _selectedItems.update { selected ->
                    selected.mapNotNull { (goodsId, specIds) ->
                        val validIds = specIds.intersect(valid[goodsId].orEmpty())
                        if (validIds.isEmpty()) null else goodsId to validIds
                    }.toMap()
                }
            }
        }
    }

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
            _selectedItems.value = emptyMap()
        }
    }

    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
    }

    fun toggleSelection(goodsId: Long, specId: Long) {
        _selectedItems.update { current ->
            val ids = current[goodsId].orEmpty().toMutableSet()
            if (!ids.add(specId)) ids.remove(specId)
            if (ids.isEmpty()) current - goodsId else current + (goodsId to ids)
        }
    }

    fun toggleSelectAll() {
        if (isAllSelected.value) {
            _selectedItems.value = emptyMap()
        } else {
            _selectedItems.value = cartItems.value.associate { cart ->
                cart.goodsId to cart.spec.map { it.id }.toSet()
            }.filterValues { it.isNotEmpty() }
        }
    }

    fun deleteSelected() {
        val selected = selectedItems.value
        viewModelScope.launch {
            selected.forEach { (goodsId, specIds) ->
                val cart = cartItems.value.firstOrNull { it.goodsId == goodsId } ?: return@forEach
                if (specIds.size == cart.spec.size) {
                    cartRepository.removeGoods(goodsId)
                } else {
                    specIds.forEach { specId -> cartRepository.removeSpec(goodsId, specId) }
                }
            }
            _selectedItems.value = emptyMap()
            _isEditing.value = false
        }
    }

    fun selectedCarts(): List<Cart> {
        val selected = selectedItems.value
        return cartItems.value.mapNotNull { cart ->
            val ids = selected[cart.goodsId].orEmpty()
            if (ids.isEmpty()) null else cart.copy(spec = cart.spec.filter { it.id in ids })
        }
    }
}
