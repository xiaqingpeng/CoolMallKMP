package com.joker.coolmallkmp.feature.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.FootprintRepository
import com.joker.coolmallkmp.core.model.entity.Footprint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage

class FootprintViewModel(
    private val footprintRepository: FootprintRepository,
) : ViewModel() {

    val footprints: StateFlow<List<Footprint>> = footprintRepository.getAllFootprints()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    fun remove(goodsId: Long) {
        viewModelScope.launch {
            runCatching { footprintRepository.removeFootprint(goodsId) }
                .onFailure { _message.emit(UiMessage.rawOrCode(it.message, MessageCode.FootprintDeleteFailed)) }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            runCatching {
                footprintRepository.clearAllFootprints()
            }.onSuccess {
                _message.emit(UiMessage.Code(MessageCode.FootprintCleared))
            }.onFailure {
                _message.emit(UiMessage.rawOrCode(it.message, MessageCode.FootprintClearFailed))
            }
        }
    }
}
