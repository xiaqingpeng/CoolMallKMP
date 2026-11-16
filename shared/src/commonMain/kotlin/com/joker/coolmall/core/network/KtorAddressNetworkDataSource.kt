package com.joker.coolmallkmp.core.network

import com.joker.coolmallkmp.core.model.common.Id
import com.joker.coolmallkmp.core.model.common.Ids
import com.joker.coolmallkmp.core.model.entity.Address
import com.joker.coolmallkmp.core.model.request.PageRequest
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
 * 收货地址 API（对齐 CoolMallKotlin AddressService）
 */
class KtorAddressNetworkDataSource(
    private val client: HttpClient,
) {
    /** 分页查询地址，和原项目地址列表的 RefreshLayout 对齐。 */
    suspend fun getAddressPage(
        request: PageRequest,
    ): NetworkResponse<NetworkPageData<Address>> =
        client.post("user/address/page") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** 查询地址列表 */
    suspend fun getAddressList(): NetworkResponse<List<Address>> =
        client.post("user/address/list") {
            contentType(ContentType.Application.Json)
            setBody(emptyMap<String, String>())
        }.body()

    /** 获取地址信息 */
    suspend fun getAddressInfo(id: Long): NetworkResponse<Address> =
        client.get("user/address/info") {
            parameter("id", id)
        }.body()

    /** 获取默认地址 */
    suspend fun getDefaultAddress(): NetworkResponse<Address?> =
        client.get("user/address/default").body()

    /** 新增地址，返回新地址 ID */
    suspend fun addAddress(address: Address): NetworkResponse<Id> =
        client.post("user/address/add") {
            contentType(ContentType.Application.Json)
            setBody(address)
        }.body()

    /** 修改地址 */
    suspend fun updateAddress(address: Address): NetworkResponse<Unit> =
        client.post("user/address/update") {
            contentType(ContentType.Application.Json)
            setBody(address)
        }.body()

    /** 删除地址（支持批量） */
    suspend fun deleteAddress(ids: List<Long>): NetworkResponse<Unit> =
        client.post("user/address/delete") {
            contentType(ContentType.Application.Json)
            setBody(Ids(ids = ids))
        }.body()
}
