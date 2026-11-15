package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.entity.UserContributor
import com.joker.coolmall.core.model.request.PageRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorUserContributorNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class UserContributorRepository(
    private val dataSource: KtorUserContributorNetworkDataSource,
) {
    suspend fun getUserContributorPage(
        request: PageRequest,
    ): NetworkResponse<NetworkPageData<UserContributor>> = withContext(Dispatchers.IO) {
        dataSource.getUserContributorPage(request)
    }
}
