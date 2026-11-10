package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.entity.ConfirmOrder
import com.joker.coolmall.core.model.entity.GoodsDetail
import com.joker.coolmall.core.model.entity.Home
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorPageNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class PageRepository(
    private val dataSource: KtorPageNetworkDataSource,
) {
    suspend fun getHomeData(): NetworkResponse<Home> = withContext(Dispatchers.IO) {
        dataSource.getHomeData()
    }

    suspend fun getGoodsDetail(goodsId: Long): NetworkResponse<GoodsDetail> =
        withContext(Dispatchers.IO) {
            dataSource.getGoodsDetail(goodsId)
        }

    suspend fun getConfirmOrder(): NetworkResponse<ConfirmOrder> =
        withContext(Dispatchers.IO) {
            dataSource.getConfirmOrder()
        }
}
