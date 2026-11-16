package com.joker.coolmallkmp.feature.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.AddressRepository
import com.joker.coolmallkmp.core.model.entity.Address
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 地址表单字段
 */
data class AddressForm(
    val id: Long = 0,
    val contact: String = "",
    val phone: String = "",
    val province: String = "",
    val city: String = "",
    val district: String = "",
    val address: String = "",
    val isDefault: Boolean = false,
) {
    val isEditMode: Boolean get() = id > 0

    /** 必填校验（返回 MessageCode，由 UI 解析） */
    val validationError: MessageCode?
        get() = when {
            contact.isBlank() -> MessageCode.AddressContactRequired
            phone.isBlank() -> MessageCode.AddressPhoneRequired
            phone.length < 6 -> MessageCode.AddressPhoneInvalid
            province.isBlank() || city.isBlank() || district.isBlank() -> MessageCode.AddressRegionRequired
            address.isBlank() -> MessageCode.AddressDetailRequired
            else -> null
        }

    val canSubmit: Boolean get() = validationError == null

    fun toEntity(): Address = Address(
        id = id,
        contact = contact.trim(),
        phone = phone.trim(),
        province = province.trim(),
        city = city.trim(),
        district = district.trim(),
        address = address.trim(),
        isDefault = isDefault,
    )
}

class AddressEditViewModel(
    private val addressRepository: AddressRepository,
    private val addressId: Long,
) : ViewModel() {

    private val _form = MutableStateFlow(AddressForm(id = addressId))
    val form: StateFlow<AddressForm> = _form.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    private val _saved = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saved: SharedFlow<Unit> = _saved.asSharedFlow()

    init {
        if (addressId > 0) {
            loadDetail()
        }
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                addressRepository.getAddressInfo(addressId)
            }.onSuccess { response ->
                _isLoading.value = false
                val data = response.data
                if (response.isSucceeded && data != null) {
                    _form.value = AddressForm(
                        id = data.id,
                        contact = data.contact,
                        phone = data.phone,
                        province = data.province,
                        city = data.city,
                        district = data.district,
                        address = data.address,
                        isDefault = data.isDefault,
                    )
                } else {
                    _message.emit(
                        response.message?.let { UiMessage.Raw(it) }
                            ?: UiMessage.Code(MessageCode.AddressLoadFailed, response.code.toString()),
                    )
                }
            }.onFailure { error ->
                _isLoading.value = false
                _message.emit(
                    error.message?.let { UiMessage.Raw(it) }
                        ?: UiMessage.Code(MessageCode.NetworkError),
                )
            }
        }
    }

    fun updateContact(value: String) = _form.update { it.copy(contact = value) }

    fun updatePhone(value: String) = _form.update { it.copy(phone = value) }

    fun updateProvince(value: String) = _form.update { it.copy(province = value) }

    fun updateCity(value: String) = _form.update { it.copy(city = value) }

    fun updateDistrict(value: String) = _form.update { it.copy(district = value) }

    fun updateAddress(value: String) = _form.update { it.copy(address = value) }

    fun updateIsDefault(value: Boolean) = _form.update { it.copy(isDefault = value) }

    /**
     * 保存：新增走 add，编辑走 update。
     */
    fun submit() {
        if (_isSubmitting.value) return
        val current = _form.value
        val error = current.validationError
        if (error != null) {
            viewModelScope.launch { _message.emit(UiMessage.Code(error)) }
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            val entity = current.toEntity()
            val result = runCatching {
                if (current.isEditMode) {
                    addressRepository.updateAddress(entity)
                } else {
                    addressRepository.addAddress(entity)
                }
            }
            _isSubmitting.value = false
            result.onSuccess { response ->
                if (response.isSucceeded) {
                    _message.emit(
                        UiMessage.Code(
                            if (current.isEditMode) MessageCode.AddressSaved else MessageCode.AddressAdded,
                        ),
                    )
                    _saved.emit(Unit)
                } else {
                    _message.emit(
                        response.message?.let { UiMessage.Raw(it) }
                            ?: UiMessage.Code(MessageCode.AddressSaveFailed, response.code.toString()),
                    )
                }
            }.onFailure { cause ->
                _message.emit(
                    cause.message?.let { UiMessage.Raw(it) }
                        ?: UiMessage.Code(MessageCode.NetworkError),
                )
            }
        }
    }
}
