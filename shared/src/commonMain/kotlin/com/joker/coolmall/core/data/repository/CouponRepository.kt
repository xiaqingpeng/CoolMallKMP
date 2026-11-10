package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.entity.Coupon
import com.joker.coolmall.core.model.request.PageRequest
import com.joker.coolmall.core.model.request.ReceiveCouponRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorCouponNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CouponRepository(
    private val dataSource: KtorCouponNetworkDataSource,
) {
    suspend fun receiveCoupon(couponId: Long): NetworkResponse<String> =
        withContext(Dispatchers.IO) {
            dataSource.receiveCoupon(ReceiveCouponRequest(couponId = couponId))
        }

    suspend fun getUserCouponPage(
        page: Int = 1,
        size: Int = 20,
    ): NetworkResponse<NetworkPageData<Coupon>> = withContext(Dispatchers.IO) {
        dataSource.getUserCouponPage(PageRequest(page = page, size = size))
    }

    suspend fun getUserCouponList(): NetworkResponse<List<Coupon>> =
        withContext(Dispatchers.IO) {
            dataSource.getUserCouponList()
        }
}
