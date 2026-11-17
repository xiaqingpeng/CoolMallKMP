package com.joker.coolmallkmp.core.data.local

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual fun applyAppLanguage(tag: String?) {
    val url = NSURL(string = UIApplicationOpenSettingsURLString)
    UIApplication.sharedApplication.openURL(
        url,
        options = emptyMap<Any?, Any?>(),
        completionHandler = null,
    )
}
