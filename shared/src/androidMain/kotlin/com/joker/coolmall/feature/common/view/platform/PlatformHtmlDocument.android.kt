package com.joker.coolmallkmp.feature.common.view.platform

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun PlatformHtmlDocument(html: String, modifier: Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                settings.domStorageEnabled = false
                webViewClient = WebViewClient()
            }
        },
        update = { view ->
            view.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        },
    )
}
