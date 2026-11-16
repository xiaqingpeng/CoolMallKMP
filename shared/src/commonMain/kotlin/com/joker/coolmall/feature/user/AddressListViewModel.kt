package com.joker.coolmallkmp.feature.user

import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.common.base.state.BaseNetWorkListUiState
import com.joker.coolmallkmp.core.common.base.state.LoadMoreState
import com.joker.coolmallkmp.core.common.base.viewmodel.BaseNetWorkListViewModel
import com.joker.coolmallkmp.core.data.repository.AddressRepository
import com.joker.coolmallkmp.core.model.entity.Address
import com.joker.coolmallkmp.core.model.request.PageRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage

sealed interface AddressListUiState {
    data object Loading : AddressListUiState
    data class Success(
        val addresses: List<Address>,
        val hasMore: Boolean = false,
    ) : AddressListUiState
    data class Error(val message: UiMessage) : AddressListUiState
}

class AddressListViewModel(
    private val addressRepository: AddressRepository,
) : BaseNetWorkListViewModel<Address>() {

    val uiState: StateFlow<AddressListUiState> = combine(
        networkUiState,
        listData,
        loadMoreState,
        errorMessage,
    ) { state, addresses, moreState, error ->
        when (state) {
            BaseNetWorkListUiState.Loading -> AddressListUiState.Loading
            BaseNetWorkListUiState.Success,
            BaseNetWorkListUiState.Empty -> AddressListUiState.Success(
                addresses = addresses,
                hasMore = moreState != LoadMoreState.NoMore,
            )
            BaseNetWorkListUiState.Error -> AddressListUiState.Error(
                error ?: UiMessage.network(null),
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddressListUiState.Loading,
    )

    private val _isActing = MutableStateFlow(false)
    val isActing: StateFlow<Boolean> = _isActing.asStateFlow()

    val isLoadingMore: StateFlow<Boolean> = loadMoreState
        .map { it == LoadMoreState.Loading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    init {
        // 子类依赖已完成初始化后再发起首次请求，避免父类初始化阶段访问未赋值的 repository。
        load()
        // 新增/编辑/删除成功后 repository.revision 自增，自动刷新（drop 掉初始值避免重复请求）
        viewModelScope.launch {
            addressRepository.revision.drop(1).collect { load() }
        }
    }

    override suspend fun requestListData(page: Int, size: Int) =
        addressRepository.getAddressPage(PageRequest(page = page, size = size))

    /**
     * 删除地址；成功后本地刷新列表。
     */
    fun deleteAddress(id: Long) {
        if (_isActing.value) return
        viewModelScope.launch {
            _isActing.value = true
            runCatching {
                addressRepository.deleteAddress(id)
            }.onSuccess { response ->
                _isActing.value = false
                if (response.isSucceeded) {
                    _message.emit(UiMessage.Code(MessageCode.AddressDeleted))
                    load()
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.AddressDeleteFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _isActing.value = false
                _message.emit(UiMessage.network(error.message))
            }
        }
    }

    /**
     * 设为默认地址：把该地址 isDefault 置 true 并提交 update。
     */
    fun setDefaultAddress(address: Address) {
        if (_isActing.value) return
        if (address.isDefault) return
        viewModelScope.launch {
            _isActing.value = true
            runCatching {
                addressRepository.updateAddress(address.copy(isDefault = true))
            }.onSuccess { response ->
                _isActing.value = false
                if (response.isSucceeded) {
                    _message.emit(UiMessage.Code(MessageCode.AddressSetDefaultOk))
                    load()
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.AddressSetDefaultFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _isActing.value = false
                _message.emit(UiMessage.network(error.message))
            }
        }
    }
}
