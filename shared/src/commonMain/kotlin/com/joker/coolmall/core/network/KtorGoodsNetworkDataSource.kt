package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.Category
import com.joker.coolmall.core.model.entity.Comment
import com.joker.coolmall.core.model.entity.Goods
import com.joker.coolmall.core.model.entity.GoodsSearchKeyword
import com.joker.coolmall.core.model.entity.GoodsSpec
import com.joker.coolmall.core.model.request.GoodsCommentPageRequest
import com.joker.coolmall.core.model.request.GoodsCommentSubmitRequest
import com.joker.coolmall.core.model.request.GoodsSearchRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 商品相关 API（从 CoolMallKotlin GoodsService 迁移为 Ktor）
 */
class KtorGoodsNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun getGoodsTypeList(): NetworkResponse<List<Category>> =
        client.post("goods/type/list") {
            contentType(ContentType.Application.Json)
            setBody(emptyMap<String, String>())
        }.body()

    suspend fun getGoodsSpecList(goodsId: Long): NetworkResponse<List<GoodsSpec>> =
        client.post("goods/spec/list") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("goodsId" to goodsId))
        }.body()

    suspend fun getSearchKeywordList(): NetworkResponse<List<GoodsSearchKeyword>> =
        client.post("goods/search/keyword/list") {
            contentType(ContentType.Application.Json)
            setBody(emptyMap<String, String>())
        }.body()

    suspend fun getGoodsPage(
        request: GoodsSearchRequest,
    ): NetworkResponse<NetworkPageData<Goods>> =
        client.post("goods/info/page") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun submitGoodsComment(
        request: GoodsCommentSubmitRequest,
    ): NetworkResponse<Boolean> =
        client.post("goods/comment/submit") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getGoodsCommentPage(
        request: GoodsCommentPageRequest,
    ): NetworkResponse<NetworkPageData<Comment>> =
        client.post("goods/comment/page") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
