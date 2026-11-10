package com.joker.coolmall.core.ui

/**
 * ViewModel 侧文案载体：固定文案用 [Code]，服务端/SDK 动态文案用 [Raw]。
 * UI 层用 [resolveUiMessage] / 各自 `when` 映射到 composeResources。
 */
sealed class UiMessage {
    data class Code(val code: MessageCode, val arg: String? = null) : UiMessage()
    data class Raw(val text: String) : UiMessage()

    companion object {
        fun code(code: MessageCode, arg: String? = null) = Code(code, arg)
        fun rawOrCode(raw: String?, fallback: MessageCode, arg: String? = null): UiMessage =
            raw?.takeIf { it.isNotBlank() }?.let { Raw(it) } ?: Code(fallback, arg)

        fun loadFailed(responseMessage: String?, code: Any?) =
            rawOrCode(responseMessage, MessageCode.LoadFailed, code?.toString())

        fun network(errorMessage: String?) =
            rawOrCode(errorMessage, MessageCode.NetworkError)
    }
}

/**
 * 跨页面共用的固定文案码。
 * 带占位参数的条目通过 [UiMessage.Code.arg] 传入（如错误码、数量）。
 */
enum class MessageCode {
    InvalidPhone,
    InvalidSmsCode,
    PasswordTooShort,
    PasswordMismatch,
    LoginFailed,
    RegisterFailed,
    SendCodeFailed,
    NetworkError,
    ThirdPartyNotConfigured,
    CodeSent,
    ResetPasswordPending,
    ResetPasswordTip,
    AddressContactRequired,
    AddressPhoneRequired,
    AddressPhoneInvalid,
    AddressRegionRequired,
    AddressDetailRequired,
    AddressLoadFailed,
    AddressSaveFailed,
    AddressSaved,
    AddressAdded,
    AddressDeleted,
    AddressDeleteFailed,
    AddressSetDefaultOk,
    AddressSetDefaultFailed,
    LoadFailed,
    SpecsLoadFailed,
    SelectSpecRequired,
    InvalidQuantity,
    AddedToCart,
    FootprintCleared,
    FootprintClearFailed,
    FootprintDeleteFailed,
    OrderCancelled,
    OrderCancelFailed,
    OrderCancelReasonUser,
    OrderConfirmedReceive,
    OrderConfirmFailed,
    OrderCommentNoGoods,
    OrderCommentNeedRating,
    OrderCommentNeedContent,
    OrderCommentSuccess,
    OrderCommentSubmitFailed,
    OrderCommentPicUrlRequired,
    OrderCommentPicUrlInvalid,
    OrderCommentPicMax,
    OrderCommentPicDup,
    OrderCommentPicUploadFailed,
    OrderConfirmNeedAddress,
    OrderConfirmNoGoods,
    OrderConfirmTitleGoods,
    OrderCreateFailed,
    OrderRefundNeedReason,
    OrderRefundSuccess,
    OrderRefundSubmitFailed,
    CsCreateSessionFailed,
    CsLoadMessagesFailed,
    CsSendFailed,
    CsUploadFailed,
    CsSessionNotReady,
    CsConnectingRetry,
    FeedbackNeedType,
    FeedbackNeedContent,
    FeedbackSubmitSuccess,
    FeedbackSubmitFailed,
    FeedbackTypeLoadFailed,
    FeedbackMaxImages,
    DocEmpty,
    CouponReceiveSuccess,
}
