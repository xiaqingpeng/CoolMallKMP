package com.joker.coolmall.feature.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.AddressRepository
import com.joker.coolmall.core.model.entity.Address
import com.joker.coolmall.core.model.request.PageRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage

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
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddressListUiState>(AddressListUiState.Loading)
    val uiState: StateFlow<AddressListUiState> = _uiState.asStateFlow()

    private val _isActing = MutableStateFlow(false)
    val isActing: StateFlow<Boolean> = _isActing.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val loadedAddresses = mutableListOf<Address>()
    private var currentPage = 1
    private val pageSize = 10
    private var hasMore = true

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    init {
        load()
        // 新增/编辑/删除成功后 repository.revision 自增，自动刷新（drop 掉初始值避免重复请求）
        viewModelScope.launch {
            addressRepository.revision.drop(1).collect { load() }
        }
    }

    fun load() {
        viewModelScope.launch {
            _isRefreshing.value = false
            _isLoadingMore.value = false
            currentPage = 1
            hasMore = true
            loadedAddresses.clear()
            _uiState.value = AddressListUiState.Loading
            runCatching {
                addressRepository.getAddressPage(PageRequest(page = 1, size = pageSize))
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    val page = response.data
                    val list = page?.list.orEmpty()
                    loadedAddresses.addAll(list)
                    hasMore = page?.pagination?.total?.let { loadedAddresses.size < it }
                        ?: (list.size >= pageSize)
                    _uiState.value = AddressListUiState.Success(
                        addresses = loadedAddresses.toList(),
                        hasMore = hasMore,
                    )
                } else {
                    _uiState.value = AddressListUiState.Error(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _uiState.value = AddressListUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    fun refresh() {
        if (_isActing.value || _isLoadingMore.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            hasMore = true
            runCatching {
                addressRepository.getAddressPage(PageRequest(page = 1, size = pageSize))
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    val page = response.data
                    val list = page?.list.orEmpty()
                    loadedAddresses.clear()
                    loadedAddresses.addAll(list)
                    hasMore = page?.pagination?.total?.let { loadedAddresses.size < it }
                        ?: (list.size >= pageSize)
                    _uiState.value = AddressListUiState.Success(
                        addresses = loadedAddresses.toList(),
                        hasMore = hasMore,
                    )
                } else {
                    _message.emit(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _message.emit(UiMessage.network(error.message))
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        if (!hasMore || _isRefreshing.value || _isLoadingMore.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            val nextPage = currentPage + 1
            runCatching {
                addressRepository.getAddressPage(PageRequest(page = nextPage, size = pageSize))
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    val page = response.data
                    val list = page?.list.orEmpty()
                    if (list.isNotEmpty()) {
                        currentPage = nextPage
                        loadedAddresses.addAll(list)
                    }
                    hasMore = page?.pagination?.total?.let { loadedAddresses.size < it }
                        ?: (list.size >= pageSize)
                    _uiState.value = AddressListUiState.Success(
                        addresses = loadedAddresses.toList(),
                        hasMore = hasMore,
                    )
                } else {
                    _message.emit(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _message.emit(UiMessage.network(error.message))
            }
            _isLoadingMore.value = false
        }
    }

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
