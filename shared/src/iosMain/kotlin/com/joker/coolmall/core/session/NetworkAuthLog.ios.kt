package com.joker.coolmall.core.session

import platform.Foundation.NSLog

actual fun networkAuthLog(message: String) {
    NSLog("CoolMallNetwork: $message")
}
