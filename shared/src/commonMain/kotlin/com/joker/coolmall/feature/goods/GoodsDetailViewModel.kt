package com.joker.coolmall.feature.goods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.CartRepository
import com.joker.coolmall.core.data.repository.FootprintRepository
import com.joker.coolmall.core.data.repository.GoodsRepository
import com.joker.coolmall.core.data.repository.PageRepository
import com.joker.coolmall.core.model.entity.GoodsDetail
import com.joker.coolmall.core.model.entity.GoodsSpec
import com.joker.coolmall.core.model.entity.SelectedGoods
import com.joker.coolmall.core.order.CheckoutDraft
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GoodsDetailUiState {
    data object Loading : GoodsDetailUiState
    data class Success(val data: GoodsDetail) : GoodsDetailUiState
    data class Error(val message: UiMessage) : GoodsDetailUiState
}

sealed interface SpecsUiState {
    data object Idle : SpecsUiState
    data object Loading : SpecsUiState
    data class Success(val specs: List<GoodsSpec>) : SpecsUiState
    data class Error(val message: UiMessage) : SpecsUiState
}

class GoodsDetailViewModel(
    private val goodsId: Long,
    private val pageRepository: PageRepository,
    private val goodsRepository: GoodsRepository,
    private val cartRepository: CartRepository,
    private val footprintRepository: FootprintRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoodsDetailUiState>(GoodsDetailUiState.Loading)
    val uiState: StateFlow<GoodsDetailUiState> = _uiState.asStateFlow()

    private val _specModalVisible = MutableStateFlow(false)
    val specModalVisible: StateFlow<Boolean> = _specModalVisible.asStateFlow()

    private val _specsUiState = MutableStateFlow<SpecsUiState>(SpecsUiState.Idle)
    val specsUiState: StateFlow<SpecsUiState> = _specsUiState.asStateFlow()

    private val _selectedSpec = MutableStateFlow<GoodsSpec?>(null)
    val selectedSpec: StateFlow<GoodsSpec?> = _selectedSpec.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _isAdding = MutableStateFlow(false)
    val isAdding: StateFlow<Boolean> = _isAdding.asStateFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = GoodsDetailUiState.Loading
            runCatching {
                pageRepository.getGoodsDetail(goodsId)
            }.onSuccess { response ->
                if (response.isSucceeded && response.data != null) {
                    _uiState.value = GoodsDetailUiState.Success(response.data)
                    // 浏览足迹：静默写入，失败不影响详情
                    runCatching {
                        footprintRepository.recordGoodsView(response.data.goodsInfo)
                    }
                } else {
                    _uiState.value = GoodsDetailUiState.Error(
                        response.message?.let { UiMessage.Raw(it) }
                            ?: UiMessage.Code(MessageCode.LoadFailed, response.code.toString()),
                    )
                }
            }.onFailure { error ->
                _uiState.value = GoodsDetailUiState.Error(
                    error.message?.let { UiMessage.Raw(it) }
                        ?: UiMessage.Code(MessageCode.NetworkError),
                )
            }
        }
    }

    fun showSpecModal() {
        _specModalVisible.value = true
        if (_quantity.value < 1) _quantity.value = 1
        loadGoodsSpecs()
    }

    fun hideSpecModal() {
        _specModalVisible.value = false
    }

    fun loadGoodsSpecs(force: Boolean = false) {
        if (!force && _specsUiState.value is SpecsUiState.Success) return
        viewModelScope.launch {
            _specsUiState.value = SpecsUiState.Loading
            runCatching {
                goodsRepository.getGoodsSpecList(goodsId)
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    val specs = response.data.orEmpty()
                    _specsUiState.value = SpecsUiState.Success(specs)
                    // 保留外部已选规格；不自动选中第一项（对齐 dump：未选时显示「未选择」）
                    val currentId = _selectedSpec.value?.id
                    _selectedSpec.value = if (currentId != null) {
                        specs.find { it.id == currentId }
                    } else {
                        null
                    }
                } else {
                    _specsUiState.value = SpecsUiState.Error(
                        response.message?.let { UiMessage.Raw(it) }
                            ?: UiMessage.Code(MessageCode.SpecsLoadFailed, response.code.toString()),
                    )
                }
            }.onFailure { error ->
                _specsUiState.value = SpecsUiState.Error(
                    error.message?.let { UiMessage.Raw(it) }
                        ?: UiMessage.Code(MessageCode.NetworkError),
                )
            }
        }
    }

    fun selectSpec(spec: GoodsSpec) {
        // 对齐 dump：再次点击已选规格则取消选择
        if (_selectedSpec.value?.id == spec.id) {
            _selectedSpec.value = null
            return
        }
        _selectedSpec.value = spec
        if (_quantity.value > spec.stock && spec.stock > 0) {
            _quantity.value = spec.stock
        }
        if (_quantity.value < 1) {
            _quantity.value = 1
        }
    }

    fun updateQuantity(value: Int) {
        val max = _selectedSpec.value?.stock?.takeIf { it > 0 } ?: 999
        _quantity.value = value.coerceIn(1, max)
    }

    fun increaseQuantity() {
        updateQuantity(_quantity.value + 1)
    }

    fun decreaseQuantity() {
        updateQuantity(_quantity.value - 1)
    }

    fun confirmAddToCart() {
        val success = _uiState.value as? GoodsDetailUiState.Success ?: return
        if (_isAdding.value) return
        val prepared = prepareSelectedGoods(success) ?: return
        viewModelScope.launch {
            _isAdding.value = true
            runCatching {
                when {
                    prepared.spec != null -> {
                        cartRepository.addGoods(
                            goods = success.data.goodsInfo,
                            spec = prepared.spec,
                            count = prepared.count,
                        )
                    }
                    else -> {
                        cartRepository.addGoods(
                            goods = success.data.goodsInfo,
                            count = prepared.count,
                        )
                    }
                }
            }.onSuccess {
                _isAdding.value = false
                hideSpecModal()
                _message.emit(UiMessage.Code(MessageCode.AddedToCart))
            }.onFailure { error ->
                _isAdding.value = false
                _message.emit(
                    when {
                        error.message == "select_spec" -> UiMessage.Code(MessageCode.SelectSpecRequired)
                        error.message != null -> UiMessage.Raw(error.message!!)
                        else -> UiMessage.Code(MessageCode.NetworkError)
                    },
                )
            }
        }
    }

    /**
     * 立即购买：写入 CheckoutDraft 后由 UI 导航确认订单页
     * @return true 表示草稿已就绪，可导航
     */
    fun buyNow(): Boolean {
        val success = _uiState.value as? GoodsDetailUiState.Success ?: return false
        val prepared = prepareSelectedGoods(success) ?: return false
        CheckoutDraft.prepareFromGoods(listOf(prepared))
        hideSpecModal()
        return true
    }

    private fun prepareSelectedGoods(success: GoodsDetailUiState.Success): SelectedGoods? {
        val specsState = _specsUiState.value
        val selected = _selectedSpec.value
        val qty = _quantity.value
        if (qty <= 0) {
            viewModelScope.launch {
                _message.emit(UiMessage.Code(MessageCode.InvalidQuantity))
            }
            return null
        }
        if (specsState is SpecsUiState.Success && specsState.specs.isNotEmpty()) {
            if (selected == null) {
                viewModelScope.launch {
                    _message.emit(UiMessage.Code(MessageCode.SelectSpecRequired))
                }
                return null
            }
            if (selected.stock <= 0) {
                viewModelScope.launch {
                    _message.emit(UiMessage.Code(MessageCode.SelectSpecRequired))
                }
                return null
            }
            return SelectedGoods(
                goodsId = success.data.goodsInfo.id,
                goodsInfo = success.data.goodsInfo,
                spec = selected,
                count = qty,
            )
        }
        // 无规格列表时按默认规格
        return SelectedGoods(
            goodsId = success.data.goodsInfo.id,
            goodsInfo = success.data.goodsInfo,
            spec = selected,
            count = qty,
        )
    }
}
