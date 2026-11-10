package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.Network
import com.joker.coolmall.core.platform.PickedImage
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * 文件上传：先取 OSS 凭证，再 multipart 上传到腾讯云。
 * 平台差异：读取本地文件字节由 [readImageBytes] expect 完成。
 */
class FileUploadRepository(
    private val commonRepository: CommonRepository,
) {
    suspend fun uploadImages(images: List<PickedImage>): NetworkResponse<List<String>> =
        withContext(Dispatchers.IO) {
            if (images.isEmpty()) {
                return@withContext NetworkResponse(data = emptyList(), code = 1000, message = "ok")
            }
            val credentialsResponse = commonRepository.getUploadCredentials()
            if (!credentialsResponse.isSucceeded || credentialsResponse.data == null) {
                return@withContext NetworkResponse(
                    data = null,
                    code = credentialsResponse.code,
                    message = credentialsResponse.message ?: "获取上传凭证失败",
                )
            }
            val oss = credentialsResponse.data
            val credentials = oss.credentials
            val uploadUrl = oss.url
            if (credentials == null || uploadUrl.isNullOrBlank()) {
                return@withContext NetworkResponse(
                    data = null,
                    code = 400,
                    message = "上传凭证无效",
                )
            }
            val urls = mutableListOf<String>()
            for (image in images) {
                val single = uploadOne(
                    image = image,
                    uploadUrl = uploadUrl,
                    tmpSecretId = credentials.tmpSecretId.orEmpty(),
                    tmpSecretKey = credentials.tmpSecretKey.orEmpty(),
                    sessionToken = credentials.sessionToken.orEmpty(),
                )
                if (!single.isSucceeded || single.data == null) {
                    return@withContext NetworkResponse(
                        data = null,
                        code = single.code,
                        message = single.message ?: "上传失败",
                    )
                }
                urls += single.data
            }
            NetworkResponse(data = urls, code = 1000, message = "批量上传成功")
        }

    private suspend fun uploadOne(
        image: PickedImage,
        uploadUrl: String,
        tmpSecretId: String,
        tmpSecretKey: String,
        sessionToken: String,
    ): NetworkResponse<String> {
        val bytes = readImageBytes(image.uri)
            ?: return NetworkResponse(data = null, code = 400, message = "无法读取图片")
        val fileName = image.fileName.ifBlank {
            "${Random.nextLong(0, Long.MAX_VALUE)}_image.jpg"
        }
        val key = "app/base/$fileName"
        return try {
            val response: HttpResponse = Network.imageHttpClient.post(uploadUrl) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("key", key)
                            append("tmpSecretId", tmpSecretId)
                            append("tmpSecretKey", tmpSecretKey)
                            append("sessionToken", sessionToken)
                            append(
                                key = "file",
                                value = bytes,
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, image.mimeType)
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"$fileName\"",
                                    )
                                },
                            )
                        }
                    )
                )
            }
            if (response.status.isSuccess()) {
                NetworkResponse(
                    data = "$uploadUrl/$key",
                    code = 1000,
                    message = "上传成功",
                )
            } else {
                NetworkResponse(
                    data = null,
                    code = response.status.value,
                    message = "上传失败: ${response.status}",
                )
            }
        } catch (e: Exception) {
            NetworkResponse(data = null, code = 500, message = e.message ?: "上传异常")
        }
    }
}

/**
 * 从平台路径 / content URI 读取图片字节。
 */
expect suspend fun readImageBytes(uri: String): ByteArray?
