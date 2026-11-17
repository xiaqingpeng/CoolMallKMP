package com.joker.coolmallkmp.feature.main.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object MainTabRequest {
    private val _requestedDestination = MutableStateFlow<TopLevelDestination?>(null)
    val requestedDestination = _requestedDestination.asStateFlow()

    fun openCart() {
        _requestedDestination.value = TopLevelDestination.CART
    }

    fun consume() {
        _requestedDestination.value = null
    }
}
