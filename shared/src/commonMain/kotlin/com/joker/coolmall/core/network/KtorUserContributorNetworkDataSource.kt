package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.UserContributor
import com.joker.coolmall.core.model.request.PageRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorUserContributorNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun getUserContributorPage(
        request: PageRequest,
    ): NetworkResponse<NetworkPageData<UserContributor>> =
        client.post("user/contributor/page") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
