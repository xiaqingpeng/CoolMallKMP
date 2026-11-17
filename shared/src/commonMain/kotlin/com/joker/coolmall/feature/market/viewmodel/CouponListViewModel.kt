package com.joker.coolmallkmp.feature.market.viewmodel

import com.joker.coolmallkmp.core.common.base.viewmodel.BaseNetWorkListViewModel
import com.joker.coolmallkmp.core.data.repository.CouponRepository
import com.joker.coolmallkmp.core.model.entity.Coupon
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkResponse

class CouponListViewModel(
    private val couponRepository: CouponRepository,
) : BaseNetWorkListViewModel<Coupon>() {

    init {
        load()
    }

    override suspend fun requestListData(
        page: Int,
        size: Int,
    ): NetworkResponse<NetworkPageData<Coupon>> =
        couponRepository.getUserCouponPage(
            page = page,
            size = size,
        )

}
