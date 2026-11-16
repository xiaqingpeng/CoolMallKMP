package com.joker.coolmallkmp.core.common.base.state

/**
 * 列表底部加载状态，与原 Android 项目的状态名称保持一致。
 */
enum class LoadMoreState {
    PullToLoad,
    Loading,
    Success,
    Error,
    NoMore,
}
