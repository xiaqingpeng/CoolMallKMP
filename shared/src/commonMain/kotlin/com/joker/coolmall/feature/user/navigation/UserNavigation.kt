package com.joker.coolmall.feature.user.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.joker.coolmall.core.model.entity.Address
import com.joker.coolmall.feature.user.AddressEditRoute
import com.joker.coolmall.feature.user.AddressListRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@Serializable
data class AddressListRouteArgs(
    val isSelectMode: Boolean = false,
)

/**
 * 地址选择结果的跨页面暂存，供订单确认页从地址列表返回时消费。
 */
object AddressSelectionStore {
    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()

    fun publish(address: Address) {
        _selectedAddress.value = address
    }

    fun consume(): Address? {
        val result = _selectedAddress.value
        _selectedAddress.value = null
        return result
    }
}

@Serializable
data class AddressEditRouteArgs(
    val addressId: Long = 0,
)

fun NavController.navigateToAddressList(isSelectMode: Boolean = false) {
    navigate(AddressListRouteArgs(isSelectMode = isSelectMode))
}

/**
 * @param addressId 0 表示新增
 */
fun NavController.navigateToAddressEdit(addressId: Long = 0) {
    navigate(AddressEditRouteArgs(addressId = addressId))
}

fun NavGraphBuilder.addressListScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit = {},
    onEditClick: (Long) -> Unit = {},
    onAddressSelected: (Address) -> Unit = {},
) {
    composable<AddressListRouteArgs> { entry ->
        val args = entry.toRoute<AddressListRouteArgs>()
        AddressListRoute(
            onBack = onBack,
            onAddClick = onAddClick,
            onEditClick = onEditClick,
            isSelectMode = args.isSelectMode,
            onAddressSelected = onAddressSelected,
        )
    }
}

fun NavGraphBuilder.addressEditScreen(
    onBack: () -> Unit,
) {
    composable<AddressEditRouteArgs> { entry ->
        val args = entry.toRoute<AddressEditRouteArgs>()
        AddressEditRoute(
            addressId = args.addressId,
            onBack = onBack,
        )
    }
}
