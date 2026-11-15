package com.joker.coolmall.core.platform

/**
 * 在支持的平台上显示短信验证码通知。
 * iOS 不做系统通知接入，actual 为空实现。
 */
expect fun notifyVerificationCode(code: String)
