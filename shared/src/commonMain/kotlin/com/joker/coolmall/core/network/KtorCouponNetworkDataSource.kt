package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.Coupon
import com.joker.coolmall.core.model.request.PageRequest
import com.joker.coolmall.core.model.request.ReceiveCouponRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 优惠券 API（对齐 CoolMallKotlin CouponService）
 */
class KtorCouponNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun receiveCoupon(request: ReceiveCouponRequest): NetworkResponse<String> =
        client.post("market/coupon/user/receive") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getUserCouponPage(request: PageRequest): NetworkResponse<NetworkPageData<Coupon>> =
        client.post("market/coupon/user/page") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getUserCouponList(): NetworkResponse<List<Coupon>> =
        client.post("market/coupon/user/list") {
            contentType(ContentType.Application.Json)
            setBody(emptyMap<String, String>())
        }.body()
}
