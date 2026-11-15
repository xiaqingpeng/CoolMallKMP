package com.joker.coolmall.core.platform

/**
 * 清理图片、网络等可再生成的应用缓存，不删除登录、主题和引导配置。
 */
expect fun clearAppCache(): Boolean
