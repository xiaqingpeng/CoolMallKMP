package com.joker.coolmallkmp.core.network

import com.joker.coolmallkmp.core.model.entity.Logistics
import com.joker.coolmallkmp.core.model.entity.Order
import com.joker.coolmallkmp.core.model.entity.OrderCount
import com.joker.coolmallkmp.core.model.request.AlipayAppPayRequest
import com.joker.coolmallkmp.core.model.request.CancelOrderRequest
import com.joker.coolmallkmp.core.model.request.CreateOrderRequest
import com.joker.coolmallkmp.core.model.request.OrderPageRequest
import com.joker.coolmallkmp.core.model.request.RefundOrderRequest
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 订单 API（对齐 CoolMallKotlin OrderService）
 * 支付宝 SDK 仅 Android actual 调用；本层只拉服务端 orderStr。
 */
class KtorOrderNetworkDataSource(
    private val client: HttpClient,
) {
    /** 创建订单 */
    suspend fun createOrder(request: CreateOrderRequest): NetworkResponse<Order> =
        client.post("order/info/create") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** 分页查询订单 */
    suspend fun getOrderPage(request: OrderPageRequest): NetworkResponse<NetworkPageData<Order>> =
        client.post("order/info/page") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** 订单详情 */
    suspend fun getOrderInfo(id: Long): NetworkResponse<Order> =
        client.get("order/info/info") {
            parameter("id", id)
        }.body()

    /** 取消订单 */
    suspend fun cancelOrder(request: CancelOrderRequest): NetworkResponse<Boolean> =
        client.post("order/info/cancel") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** 确认收货 */
    suspend fun confirmReceive(orderId: Long): NetworkResponse<Boolean> =
        client.get("order/info/confirm") {
            parameter("orderId", orderId)
        }.body()

    /** 订单物流 */
    suspend fun getOrderLogistics(orderId: Long): NetworkResponse<Logistics> =
        client.get("order/info/logistics") {
            parameter("orderId", orderId)
        }.body()

    /** 申请退款 */
    suspend fun refundOrder(request: RefundOrderRequest): NetworkResponse<Boolean> =
        client.post("order/info/refund") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** 支付宝 App 支付参数（orderStr） */
    suspend fun alipayAppPay(orderId: Long): NetworkResponse<String> =
        client.post("order/pay/alipayAppPay") {
            contentType(ContentType.Application.Json)
            setBody(AlipayAppPayRequest(orderId = orderId))
        }.body()

    /** 用户各状态订单数量 */
    suspend fun getUserOrderCount(): NetworkResponse<OrderCount> =
        client.get("order/info/userCount").body()
}
