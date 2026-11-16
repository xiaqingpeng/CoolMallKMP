package com.joker.coolmallkmp.core.data.repository

import com.joker.coolmallkmp.core.model.response.NetworkResponse
import com.joker.coolmallkmp.core.network.Network
import com.joker.coolmallkmp.core.platform.PickedImage
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import com.joker.coolmallkmp.core.session.networkAuthLog
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
            networkAuthLog("feedback image upload start count=${images.size}")
            val credentialsResponse = commonRepository.getUploadCredentials()
            if (!credentialsResponse.isSucceeded || credentialsResponse.data == null) {
                networkAuthLog(
                    "feedback image credentials failed code=${credentialsResponse.code} " +
                        "message=${credentialsResponse.message.orEmpty()}",
                )
                return@withContext NetworkResponse(
                    data = null,
                    code = credentialsResponse.code,
                    message = credentialsResponse.message ?: "获取上传凭证失败",
                )
            }
            val oss = credentialsResponse.data
            val credentials = oss.credentials
            val uploadUrl = oss.url?.trim()?.trimEnd('/')
            if (credentials == null || uploadUrl.isNullOrBlank()) {
                networkAuthLog("feedback image credentials invalid")
                return@withContext NetworkResponse(
                    data = null,
                    code = 400,
                    message = "上传凭证无效",
                )
            }
            val urls = mutableListOf<String>()
            for (image in images) {
                networkAuthLog(
                    "feedback image read uriType=${image.uri.substringBefore(':', "path")} " +
                        "fileName=${image.fileName} mime=${image.mimeType}",
                )
                val single = uploadOne(
                    image = image,
                    uploadUrl = uploadUrl,
                    tmpSecretId = credentials.tmpSecretId.orEmpty(),
                    tmpSecretKey = credentials.tmpSecretKey.orEmpty(),
                    sessionToken = credentials.sessionToken.orEmpty(),
                )
                if (!single.isSucceeded || single.data == null) {
                    networkAuthLog(
                        "feedback image upload failed code=${single.code} " +
                            "message=${single.message.orEmpty()}",
                    )
                    return@withContext NetworkResponse(
                        data = null,
                        code = single.code,
                        message = single.message ?: "上传失败",
                    )
                }
                urls += single.data
            }
            networkAuthLog("feedback image upload success count=${urls.size}")
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
        val fileName = randomUploadFileName(image.mimeType)
        val key = "app/base/$fileName"
        networkAuthLog("feedback image bytes=${bytes.size} key=$key")
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
                val responseBody = response.bodyAsText().take(300)
                networkAuthLog(
                    "feedback image upload response http=${response.status.value} " +
                        "body=${responseBody.ifBlank { "<empty>" }}",
                )
                NetworkResponse(
                    // Keep the public URL format identical to CoolMallKotlin:
                    // <credential url>/app/base/<generated file name>.
                    data = "$uploadUrl/$key",
                    code = 1000,
                    message = "上传成功",
                )
            } else {
                val responseBody = response.bodyAsText().take(300)
                networkAuthLog(
                    "feedback image upload response http=${response.status.value} " +
                        "body=${responseBody.ifBlank { "<empty>" }}",
                )
                NetworkResponse(
                    data = null,
                    code = response.status.value,
                    message = "上传失败: ${response.status} $responseBody",
                )
            }
        } catch (e: Exception) {
            NetworkResponse(data = null, code = 500, message = e.message ?: "上传异常")
        }
    }

    private fun randomUploadFileName(mimeType: String): String {
        val extension = when {
            mimeType.contains("png", ignoreCase = true) -> "png"
            mimeType.contains("webp", ignoreCase = true) -> "webp"
            mimeType.contains("gif", ignoreCase = true) -> "gif"
            else -> "jpg"
        }
        return "${Random.nextLong(0, Long.MAX_VALUE)}_image.$extension"
    }
}

/**
 * 从平台路径 / content URI 读取图片字节。
 */
expect suspend fun readImageBytes(uri: String): ByteArray?
