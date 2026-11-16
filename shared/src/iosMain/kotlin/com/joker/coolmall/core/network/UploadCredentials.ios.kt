package com.joker.coolmallkmp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders

actual suspend fun requestUploadCredentials(
    client: HttpClient,
    url: String,
    token: String,
): UploadCredentialsHttpResponse {
    val response = client.post(url) {
        headers.remove(HttpHeaders.Authorization)
        headers[HttpHeaders.Authorization] = token
        headers.remove(HttpHeaders.ContentType)
        setBody(
            MultiPartFormDataContent(
                formData {
                    append("file", "")
                },
            ),
        )
    }
    return UploadCredentialsHttpResponse(
        statusCode = response.status.value,
        contentType = response.headers[HttpHeaders.ContentType].orEmpty(),
        body = response.bodyAsText(),
    )
}
