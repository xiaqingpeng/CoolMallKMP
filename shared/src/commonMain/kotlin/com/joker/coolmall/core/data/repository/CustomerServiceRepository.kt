package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.entity.CsMsg
import com.joker.coolmall.core.model.entity.CsSession
import com.joker.coolmall.core.model.request.MessagePageRequest
import com.joker.coolmall.core.model.request.ReadMessageRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorCustomerServiceNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CustomerServiceRepository(
    private val dataSource: KtorCustomerServiceNetworkDataSource,
) {
    suspend fun createSession(): NetworkResponse<CsSession> =
        withContext(Dispatchers.IO) {
            dataSource.createSession()
        }

    suspend fun getSessionDetail(): NetworkResponse<CsSession> =
        withContext(Dispatchers.IO) {
            dataSource.getSessionDetail()
        }

    suspend fun readMessage(ids: List<Long>): NetworkResponse<Boolean> =
        withContext(Dispatchers.IO) {
            dataSource.readMessage(ReadMessageRequest(ids = ids))
        }

    suspend fun getMessagePage(
        sessionId: Long,
        page: Int,
        size: Int = 10,
    ): NetworkResponse<NetworkPageData<CsMsg>> =
        withContext(Dispatchers.IO) {
            dataSource.getMessagePage(
                MessagePageRequest(
                    sessionId = sessionId,
                    page = page,
                    size = size,
                )
            )
        }

    suspend fun getUnreadCount(): NetworkResponse<Int> =
        withContext(Dispatchers.IO) {
            dataSource.getUnreadCount()
        }
}
