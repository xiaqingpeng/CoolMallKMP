package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.ConfirmOrder
import com.joker.coolmall.core.model.entity.GoodsDetail
import com.joker.coolmall.core.model.entity.Home
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * 页面相关 API（从 CoolMallKotlin PageService 迁移为 Ktor）
 */
class KtorPageNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun getHomeData(): NetworkResponse<Home> =
        client.get("page/home").body()

    suspend fun getGoodsDetail(goodsId: Long): NetworkResponse<GoodsDetail> =
        client.get("page/goodsDetail") {
            parameter("goodsId", goodsId)
        }.body()

    suspend fun getConfirmOrder(): NetworkResponse<ConfirmOrder> =
        client.get("page/confirmOrder").body()
}
