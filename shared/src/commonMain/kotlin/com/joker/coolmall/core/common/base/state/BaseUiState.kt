package com.joker.coolmallkmp.core.common.base.state

/**
 * 与 CoolMallKotlin 对齐的基础 UI 状态标记。
 *
 * 具体页面可以继续扩展自己的状态，但公共列表和网络请求应优先使用这些类型。
 */
sealed interface BaseUiState<out T>
