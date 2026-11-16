package com.joker.coolmallkmp.feature.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.WebKit.WKWebView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformHtmlDocument(html: String, modifier: Modifier) {
    val loadedHtml = remember { mutableStateOf<String?>(null) }
    val styledHtml = remember(html) {
        val style = """
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    background: #FFFFFF;
                    color: #181818;
                    font-family: -apple-system, BlinkMacSystemFont, "Helvetica Neue", sans-serif;
                    font-size: 16px;
                    line-height: 1.65;
                    overflow-wrap: anywhere;
                }
                body {
                    padding: 16px;
                    box-sizing: border-box;
                }
                img, video {
                    max-width: 100%;
                    height: auto;
                }
                table {
                    max-width: 100%;
                    display: block;
                    overflow-x: auto;
                }
            </style>
        """.trimIndent()
        if (html.contains("</head>", ignoreCase = true)) {
            html.replaceFirst(Regex("(?i)</head>"), "$style</head>")
        } else {
            "$style$html"
        }
    }
    UIKitView(
        modifier = modifier,
        factory = { WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) },
        update = { webView ->
            if (loadedHtml.value != styledHtml) {
                webView.loadHTMLString(styledHtml, baseURL = null)
                loadedHtml.value = styledHtml
            }
        },
    )
}
