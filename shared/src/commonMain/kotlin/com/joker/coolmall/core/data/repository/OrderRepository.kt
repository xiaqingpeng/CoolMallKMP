package com.joker.coolmallkmp.core.data.repository

import com.joker.coolmallkmp.core.model.entity.Logistics
import com.joker.coolmallkmp.core.model.entity.Order
import com.joker.coolmallkmp.core.model.entity.OrderCount
import com.joker.coolmallkmp.core.model.request.CancelOrderRequest
import com.joker.coolmallkmp.core.model.request.CreateOrderRequest
import com.joker.coolmallkmp.core.model.request.OrderPageRequest
import com.joker.coolmallkmp.core.model.request.RefundOrderRequest
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import com.joker.coolmallkmp.core.network.KtorOrderNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class OrderRepository(
    private val dataSource: KtorOrderNetworkDataSource,
) {
    suspend fun createOrder(request: CreateOrderRequest): NetworkResponse<Order> =
        withContext(Dispatchers.IO) {
            dataSource.createOrder(request)
        }

    suspend fun getOrderPage(request: OrderPageRequest): NetworkResponse<NetworkPageData<Order>> =
        withContext(Dispatchers.IO) {
            dataSource.getOrderPage(request)
        }

    suspend fun getOrderInfo(id: Long): NetworkResponse<Order> =
        withContext(Dispatchers.IO) {
            dataSource.getOrderInfo(id)
        }

    suspend fun cancelOrder(orderId: Long, remark: String): NetworkResponse<Boolean> =
        withContext(Dispatchers.IO) {
            dataSource.cancelOrder(CancelOrderRequest(orderId = orderId, remark = remark))
        }

    suspend fun confirmReceive(orderId: Long): NetworkResponse<Boolean> =
        withContext(Dispatchers.IO) {
            dataSource.confirmReceive(orderId)
        }

    suspend fun getOrderLogistics(orderId: Long): NetworkResponse<Logistics> =
        withContext(Dispatchers.IO) {
            dataSource.getOrderLogistics(orderId)
        }

    suspend fun refundOrder(orderId: Long, reason: String): NetworkResponse<Boolean> =
        withContext(Dispatchers.IO) {
            dataSource.refundOrder(RefundOrderRequest(orderId = orderId, reason = reason))
        }

    /** 获取支付宝 App 支付 orderStr；真正调 SDK 在平台层 */
    suspend fun alipayAppPay(orderId: Long): NetworkResponse<String> =
        withContext(Dispatchers.IO) {
            dataSource.alipayAppPay(orderId)
        }

    /** 用户订单各状态数量（「我的」角标） */
    suspend fun getUserOrderCount(): NetworkResponse<OrderCount> =
        withContext(Dispatchers.IO) {
            dataSource.getUserOrderCount()
        }
}
