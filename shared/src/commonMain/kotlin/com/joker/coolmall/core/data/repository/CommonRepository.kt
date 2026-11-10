package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.entity.DictItem
import com.joker.coolmall.core.model.entity.OssUpload
import com.joker.coolmall.core.model.request.DictDataRequest
import com.joker.coolmall.core.model.response.DictDataResponse
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorCommonNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * 通用仓库：字典、系统参数、上传凭证。
 */
class CommonRepository(
    private val dataSource: KtorCommonNetworkDataSource,
) {
    suspend fun getDictData(request: DictDataRequest): NetworkResponse<DictDataResponse> =
        withContext(Dispatchers.IO) {
            dataSource.getDictData(request)
        }

    /**
     * 订单退款原因字典。
     */
    suspend fun getOrderRefundReasons(): NetworkResponse<List<DictItem>> =
        getDictData(DictDataRequest(types = listOf("orderRefundReason")))
            .let { response ->
                if (response.isSucceeded) {
                    NetworkResponse(
                        data = response.data?.orderRefundReason.orEmpty(),
                        code = response.code,
                        message = response.message,
                    )
                } else {
                    NetworkResponse(
                        data = null,
                        code = response.code,
                        message = response.message,
                    )
                }
            }

    /**
     * 订单取消原因字典。
     */
    suspend fun getOrderCancelReasons(): NetworkResponse<List<DictItem>> =
        getDictData(DictDataRequest(types = listOf("orderCancelReason")))
            .let { response ->
                if (response.isSucceeded) {
                    NetworkResponse(
                        data = response.data?.orderCancelReason.orEmpty(),
                        code = response.code,
                        message = response.message,
                    )
                } else {
                    NetworkResponse(
                        data = null,
                        code = response.code,
                        message = response.message,
                    )
                }
            }

    /**
     * 意见反馈类型字典。
     */
    suspend fun getFeedbackTypes(): NetworkResponse<List<DictItem>> =
        getDictData(DictDataRequest(types = listOf("feedbackType")))
            .let { response ->
                if (response.isSucceeded) {
                    NetworkResponse(
                        data = response.data?.feedbackType.orEmpty(),
                        code = response.code,
                        message = response.message,
                    )
                } else {
                    NetworkResponse(
                        data = null,
                        code = response.code,
                        message = response.message,
                    )
                }
            }

    suspend fun getParam(key: String): NetworkResponse<String> = withContext(Dispatchers.IO) {
        dataSource.getParam(key)
    }

    suspend fun getUploadCredentials(): NetworkResponse<OssUpload> =
        withContext(Dispatchers.IO) {
            dataSource.uploadFile()
        }
}
