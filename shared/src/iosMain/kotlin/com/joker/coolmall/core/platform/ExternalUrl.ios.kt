package com.joker.coolmall.core.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openExternalUrl(url: String) {
    NSURL.URLWithString(url)?.let {
        UIApplication.sharedApplication.openURL(it)
    }
}
