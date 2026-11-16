package com.joker.coolmallkmp.core.network

import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * The original Android app uses Retrofit/OkHttp for this empty POST.
 * Keep this endpoint on native OkHttp because the backend rejects the
 * equivalent Ktor request shape with code 500.
 */
actual suspend fun requestUploadCredentials(
    client: HttpClient,
    url: String,
    token: String,
): UploadCredentialsHttpResponse = withContext(Dispatchers.IO) {
    val request = Request.Builder()
        .url(url)
        .header("Authorization", token)
        .header("Accept", "*/*")
        .post(
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                // Apifox declares a multipart/form-data body. OkHttp rejects
                // an empty multipart, so send one empty placeholder part.
                .addFormDataPart("file", "", ByteArray(0).toRequestBody(null))
                .build(),
        )
        .build()
    OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
        .newCall(request)
        .execute()
        .use { response ->
        UploadCredentialsHttpResponse(
            statusCode = response.code,
            contentType = response.header("Content-Type").orEmpty(),
            body = response.body.string(),
        )
    }
}
