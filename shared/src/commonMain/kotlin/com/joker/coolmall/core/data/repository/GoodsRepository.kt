package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.entity.Category
import com.joker.coolmall.core.model.entity.CategoryTree
import com.joker.coolmall.core.model.entity.Goods
import com.joker.coolmall.core.model.entity.GoodsSearchKeyword
import com.joker.coolmall.core.model.entity.GoodsSpec
import com.joker.coolmall.core.model.request.GoodsCommentSubmitRequest
import com.joker.coolmall.core.model.request.GoodsSearchRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorGoodsNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GoodsRepository(
    private val dataSource: KtorGoodsNetworkDataSource,
) {
    suspend fun getGoodsTypeList(): NetworkResponse<List<Category>> = withContext(Dispatchers.IO) {
        dataSource.getGoodsTypeList()
    }

    suspend fun getCategoryTree(): NetworkResponse<List<CategoryTree>> {
        val response = getGoodsTypeList()
        return NetworkResponse(
            data = response.data?.let(CategoryTree::fromCategories).orEmpty(),
            code = response.code,
            message = response.message,
        )
    }

    suspend fun getGoodsSpecList(goodsId: Long): NetworkResponse<List<GoodsSpec>> =
        withContext(Dispatchers.IO) {
            dataSource.getGoodsSpecList(goodsId)
        }

    suspend fun getSearchKeywordList(): NetworkResponse<List<GoodsSearchKeyword>> =
        withContext(Dispatchers.IO) {
            dataSource.getSearchKeywordList()
        }

    suspend fun getGoodsPage(
        request: GoodsSearchRequest,
    ): NetworkResponse<NetworkPageData<Goods>> = withContext(Dispatchers.IO) {
        dataSource.getGoodsPage(request)
    }

    suspend fun submitGoodsComment(
        request: GoodsCommentSubmitRequest,
    ): NetworkResponse<Boolean> = withContext(Dispatchers.IO) {
        dataSource.submitGoodsComment(request)
    }
}
