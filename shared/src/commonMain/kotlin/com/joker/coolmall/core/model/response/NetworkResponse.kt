package com.joker.coolmall.core.model.response

import kotlinx.serialization.Serializable

/**
 * 解析网络响应
 *
 * @param T 数据类型
 * @param data 真实数据
 * @param code 状态码 等于1000表示成功
 * @param message 出错的提示信息
 */
@Serializable
data class NetworkResponse<T>(
    val data: T? = null,
    val code: Int = 1000,
    val message: String? = null,
) {
    val isSucceeded: Boolean
        get() = code == 1000
}
