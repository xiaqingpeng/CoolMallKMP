package com.joker.coolmallkmp.core.data.repository

import com.joker.coolmallkmp.core.model.entity.UserContributor
import com.joker.coolmallkmp.core.model.request.PageRequest
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import com.joker.coolmallkmp.core.network.KtorUserContributorNetworkDataSource
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
