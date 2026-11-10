package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.entity.Feedback
import com.joker.coolmall.core.model.request.FeedbackSubmitRequest
import com.joker.coolmall.core.model.request.PageRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorFeedbackNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class FeedbackRepository(
    private val dataSource: KtorFeedbackNetworkDataSource,
) {
    suspend fun submitFeedback(params: FeedbackSubmitRequest): NetworkResponse<Boolean> =
        withContext(Dispatchers.IO) {
            dataSource.submitFeedback(params)
        }

    suspend fun getFeedbackPage(params: PageRequest): NetworkResponse<NetworkPageData<Feedback>> =
        withContext(Dispatchers.IO) {
            dataSource.getFeedbackPage(params)
        }
}
