package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.common.Id
import com.joker.coolmall.core.model.entity.Address
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorAddressNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class AddressRepository(
    private val dataSource: KtorAddressNetworkDataSource,
) {
    /**
     * 地址变更版本号：增删改后自增，列表页据此自动刷新。
     */
    private val _revision = MutableStateFlow(0)
    val revision: StateFlow<Int> = _revision.asStateFlow()

    suspend fun getAddressList(): NetworkResponse<List<Address>> = withContext(Dispatchers.IO) {
        dataSource.getAddressList()
    }

    suspend fun getAddressInfo(id: Long): NetworkResponse<Address> = withContext(Dispatchers.IO) {
        dataSource.getAddressInfo(id)
    }

    suspend fun getDefaultAddress(): NetworkResponse<Address?> = withContext(Dispatchers.IO) {
        dataSource.getDefaultAddress()
    }

    suspend fun addAddress(address: Address): NetworkResponse<Id> = withContext(Dispatchers.IO) {
        dataSource.addAddress(address)
    }.also { if (it.isSucceeded) notifyChanged() }

    suspend fun updateAddress(address: Address): NetworkResponse<Unit> =
        withContext(Dispatchers.IO) {
            dataSource.updateAddress(address)
        }.also { if (it.isSucceeded) notifyChanged() }

    suspend fun deleteAddress(id: Long): NetworkResponse<Unit> = withContext(Dispatchers.IO) {
        dataSource.deleteAddress(listOf(id))
    }.also { if (it.isSucceeded) notifyChanged() }

    suspend fun deleteAddresses(ids: List<Long>): NetworkResponse<Unit> =
        withContext(Dispatchers.IO) {
            dataSource.deleteAddress(ids)
        }.also { if (it.isSucceeded) notifyChanged() }

    private fun notifyChanged() {
        _revision.update { it + 1 }
    }
}
