package com.joker.coolmallkmp.feature.common.view.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSError
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject
import kotlinx.coroutines.delay

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebPage(
    url: String,
    reloadToken: Int,
    onTitleChange: (String) -> Unit,
    onProgressChange: (Int) -> Unit,
    modifier: Modifier,
) {
    val lastReloadToken = remember { mutableStateOf(-1) }
    val lastUrl = remember { mutableStateOf<String?>(null) }
    val webViewState = remember { mutableStateOf<WKWebView?>(null) }
    LaunchedEffect(webViewState.value) {
        val webView = webViewState.value ?: return@LaunchedEffect
        while (true) {
            onProgressChange((webView.estimatedProgress * 100.0).toInt().coerceIn(0, 100))
            delay(100)
        }
    }
    UIKitView(
        modifier = modifier,
        factory = {
            WKWebView(
                frame = CGRectMake(0.0, 0.0, 0.0, 0.0),
                configuration = WKWebViewConfiguration(),
            ).apply {
                navigationDelegate = object : NSObject(), WKNavigationDelegateProtocol {
                    @ObjCSignatureOverride
                    override fun webView(
                        webView: WKWebView,
                        didStartProvisionalNavigation: WKNavigation?,
                    ) {
                        onProgressChange(10)
                    }

                    @ObjCSignatureOverride
                    override fun webView(
                        webView: WKWebView,
                        didFinishNavigation: WKNavigation?,
                    ) {
                        onProgressChange(100)
                        webView.title?.takeIf { it.isNotBlank() }?.let(onTitleChange)
                    }

                    override fun webView(
                        webView: WKWebView,
                        didFailNavigation: WKNavigation?,
                        withError: NSError,
                    ) {
                        onProgressChange(100)
                    }
                }
                loadRequest(NSURLRequest.requestWithURL(NSURL(string = url)))
            }.also { webViewState.value = it }
        },
        update = { webView ->
            if (lastUrl.value != url || webView.URL?.absoluteString != url) {
                NSURL(string = url).let { webView.loadRequest(NSURLRequest.requestWithURL(it)) }
                lastUrl.value = url
            }
            if (lastReloadToken.value != reloadToken) {
                lastReloadToken.value = reloadToken
                if (reloadToken > 0) webView.reload()
            }
        },
    )
}
