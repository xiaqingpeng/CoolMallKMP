package com.joker.coolmallkmp.feature.goods.viewmodel

import com.joker.coolmallkmp.core.common.base.viewmodel.BaseNetWorkListViewModel
import com.joker.coolmallkmp.core.data.repository.GoodsRepository
import com.joker.coolmallkmp.core.model.entity.Comment
import com.joker.coolmallkmp.core.model.request.GoodsCommentPageRequest
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkResponse

class GoodsCommentViewModel(
    private val goodsId: Long,
    private val repository: GoodsRepository,
) : BaseNetWorkListViewModel<Comment>() {
    init {
        load()
    }

    override suspend fun requestListData(
        page: Int,
        size: Int,
    ): NetworkResponse<NetworkPageData<Comment>> =
        repository.getGoodsCommentPage(
            GoodsCommentPageRequest(
                goodsId = goodsId.toString(),
                page = page,
                size = size,
            ),
        )
}
