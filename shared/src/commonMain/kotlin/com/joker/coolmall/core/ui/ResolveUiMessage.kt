package com.joker.coolmall.core.ui

import androidx.compose.runtime.Composable
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.address_deleted
import myapplication.shared.generated.resources.address_delete_failed
import myapplication.shared.generated.resources.address_err_contact_required
import myapplication.shared.generated.resources.address_err_detail_required
import myapplication.shared.generated.resources.address_err_load_failed
import myapplication.shared.generated.resources.address_err_phone_invalid
import myapplication.shared.generated.resources.address_err_phone_required
import myapplication.shared.generated.resources.address_err_region_required
import myapplication.shared.generated.resources.address_err_save_failed
import myapplication.shared.generated.resources.address_msg_added
import myapplication.shared.generated.resources.address_msg_saved
import myapplication.shared.generated.resources.address_set_default_failed
import myapplication.shared.generated.resources.address_set_default_ok
import myapplication.shared.generated.resources.auth_code_sent
import myapplication.shared.generated.resources.auth_err_invalid_phone
import myapplication.shared.generated.resources.auth_err_invalid_sms_code
import myapplication.shared.generated.resources.auth_err_login_failed
import myapplication.shared.generated.resources.auth_err_password_mismatch
import myapplication.shared.generated.resources.auth_err_password_too_short
import myapplication.shared.generated.resources.auth_err_register_failed
import myapplication.shared.generated.resources.auth_err_send_code_failed
import myapplication.shared.generated.resources.auth_reset_pending
import myapplication.shared.generated.resources.auth_reset_tip
import myapplication.shared.generated.resources.auth_third_party_unavailable
import myapplication.shared.generated.resources.common_load_failed
import myapplication.shared.generated.resources.common_network_error
import myapplication.shared.generated.resources.coupon_receive_success
import myapplication.shared.generated.resources.cs_connecting_retry
import myapplication.shared.generated.resources.cs_create_session_failed
import myapplication.shared.generated.resources.cs_load_messages_failed
import myapplication.shared.generated.resources.cs_send_failed
import myapplication.shared.generated.resources.cs_session_not_ready
import myapplication.shared.generated.resources.cs_upload_failed
import myapplication.shared.generated.resources.doc_empty
import myapplication.shared.generated.resources.feedback_max_images
import myapplication.shared.generated.resources.feedback_need_content
import myapplication.shared.generated.resources.feedback_need_type
import myapplication.shared.generated.resources.feedback_submit_failed
import myapplication.shared.generated.resources.feedback_submit_success
import myapplication.shared.generated.resources.feedback_type_load_failed
import myapplication.shared.generated.resources.feedback_upload_failed
import myapplication.shared.generated.resources.footprint_clear_failed
import myapplication.shared.generated.resources.footprint_cleared
import myapplication.shared.generated.resources.footprint_delete_failed
import myapplication.shared.generated.resources.goods_err_invalid_quantity
import myapplication.shared.generated.resources.goods_err_select_spec
import myapplication.shared.generated.resources.goods_err_specs_load_failed
import myapplication.shared.generated.resources.goods_msg_added_to_cart
import myapplication.shared.generated.resources.order_cancel_failed
import myapplication.shared.generated.resources.order_cancel_reason_user
import myapplication.shared.generated.resources.order_cancelled
import myapplication.shared.generated.resources.order_comment_need_content
import myapplication.shared.generated.resources.order_comment_need_rating
import myapplication.shared.generated.resources.order_comment_no_goods
import myapplication.shared.generated.resources.order_comment_pic_dup
import myapplication.shared.generated.resources.order_comment_pic_max
import myapplication.shared.generated.resources.order_comment_pic_upload_failed
import myapplication.shared.generated.resources.order_comment_pic_url_invalid
import myapplication.shared.generated.resources.order_comment_pic_url_required
import myapplication.shared.generated.resources.order_comment_submit_failed
import myapplication.shared.generated.resources.order_comment_success
import myapplication.shared.generated.resources.order_confirm_failed
import myapplication.shared.generated.resources.order_confirm_need_address
import myapplication.shared.generated.resources.order_confirm_no_goods
import myapplication.shared.generated.resources.order_confirm_title_goods
import myapplication.shared.generated.resources.order_confirmed_receive
import myapplication.shared.generated.resources.order_create_failed
import myapplication.shared.generated.resources.order_refund_need_reason
import myapplication.shared.generated.resources.order_refund_submit_failed
import myapplication.shared.generated.resources.order_refund_success
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * Composable 内解析 [UiMessage] 为本地化字符串。
 */
@Composable
fun resolveUiMessage(message: UiMessage): String = when (message) {
    is UiMessage.Raw -> message.text
    is UiMessage.Code -> resolveMessageCode(message.code, message.arg)
}

@Composable
fun resolveMessageCode(code: MessageCode, arg: String? = null): String {
    val safeArg = arg.orEmpty()
    val intArg = arg?.toIntOrNull() ?: 0
    return when (code) {
        MessageCode.InvalidPhone -> stringResource(Res.string.auth_err_invalid_phone)
        MessageCode.InvalidSmsCode -> stringResource(Res.string.auth_err_invalid_sms_code)
        MessageCode.PasswordTooShort -> stringResource(Res.string.auth_err_password_too_short)
        MessageCode.PasswordMismatch -> stringResource(Res.string.auth_err_password_mismatch)
        MessageCode.LoginFailed -> stringResource(Res.string.auth_err_login_failed, safeArg)
        MessageCode.RegisterFailed -> stringResource(Res.string.auth_err_register_failed, safeArg)
        MessageCode.SendCodeFailed -> stringResource(Res.string.auth_err_send_code_failed, safeArg)
        MessageCode.NetworkError -> stringResource(Res.string.common_network_error)
        MessageCode.ThirdPartyNotConfigured -> stringResource(Res.string.auth_third_party_unavailable)
        MessageCode.CodeSent -> stringResource(Res.string.auth_code_sent)
        MessageCode.ResetPasswordPending -> stringResource(Res.string.auth_reset_pending)
        MessageCode.ResetPasswordTip -> stringResource(Res.string.auth_reset_tip)
        MessageCode.AddressContactRequired -> stringResource(Res.string.address_err_contact_required)
        MessageCode.AddressPhoneRequired -> stringResource(Res.string.address_err_phone_required)
        MessageCode.AddressPhoneInvalid -> stringResource(Res.string.address_err_phone_invalid)
        MessageCode.AddressRegionRequired -> stringResource(Res.string.address_err_region_required)
        MessageCode.AddressDetailRequired -> stringResource(Res.string.address_err_detail_required)
        MessageCode.AddressLoadFailed -> stringResource(Res.string.address_err_load_failed, safeArg)
        MessageCode.AddressSaveFailed -> stringResource(Res.string.address_err_save_failed, safeArg)
        MessageCode.AddressSaved -> stringResource(Res.string.address_msg_saved)
        MessageCode.AddressAdded -> stringResource(Res.string.address_msg_added)
        MessageCode.AddressDeleted -> stringResource(Res.string.address_deleted)
        MessageCode.AddressDeleteFailed -> stringResource(Res.string.address_delete_failed, safeArg)
        MessageCode.AddressSetDefaultOk -> stringResource(Res.string.address_set_default_ok)
        MessageCode.AddressSetDefaultFailed -> stringResource(Res.string.address_set_default_failed, safeArg)
        MessageCode.LoadFailed -> stringResource(Res.string.common_load_failed, safeArg)
        MessageCode.SpecsLoadFailed -> stringResource(Res.string.goods_err_specs_load_failed, safeArg)
        MessageCode.SelectSpecRequired -> stringResource(Res.string.goods_err_select_spec)
        MessageCode.InvalidQuantity -> stringResource(Res.string.goods_err_invalid_quantity)
        MessageCode.AddedToCart -> stringResource(Res.string.goods_msg_added_to_cart)
        MessageCode.FootprintCleared -> stringResource(Res.string.footprint_cleared)
        MessageCode.FootprintClearFailed -> stringResource(Res.string.footprint_clear_failed)
        MessageCode.FootprintDeleteFailed -> stringResource(Res.string.footprint_delete_failed)
        MessageCode.OrderCancelled -> stringResource(Res.string.order_cancelled)
        MessageCode.OrderCancelFailed -> stringResource(Res.string.order_cancel_failed, safeArg)
        MessageCode.OrderCancelReasonUser -> stringResource(Res.string.order_cancel_reason_user)
        MessageCode.OrderConfirmedReceive -> stringResource(Res.string.order_confirmed_receive)
        MessageCode.OrderConfirmFailed -> stringResource(Res.string.order_confirm_failed, safeArg)
        MessageCode.OrderCommentNoGoods -> stringResource(Res.string.order_comment_no_goods)
        MessageCode.OrderCommentNeedRating -> stringResource(Res.string.order_comment_need_rating)
        MessageCode.OrderCommentNeedContent -> stringResource(Res.string.order_comment_need_content)
        MessageCode.OrderCommentSuccess -> stringResource(Res.string.order_comment_success)
        MessageCode.OrderCommentSubmitFailed -> stringResource(Res.string.order_comment_submit_failed, safeArg)
        MessageCode.OrderCommentPicUrlRequired -> stringResource(Res.string.order_comment_pic_url_required)
        MessageCode.OrderCommentPicUrlInvalid -> stringResource(Res.string.order_comment_pic_url_invalid)
        MessageCode.OrderCommentPicMax -> stringResource(Res.string.order_comment_pic_max, intArg)
        MessageCode.OrderCommentPicDup -> stringResource(Res.string.order_comment_pic_dup)
        MessageCode.OrderCommentPicUploadFailed -> stringResource(Res.string.order_comment_pic_upload_failed)
        MessageCode.OrderConfirmNeedAddress -> stringResource(Res.string.order_confirm_need_address)
        MessageCode.OrderConfirmNoGoods -> stringResource(Res.string.order_confirm_no_goods)
        MessageCode.OrderConfirmTitleGoods -> stringResource(Res.string.order_confirm_title_goods)
        MessageCode.OrderCreateFailed -> stringResource(Res.string.order_create_failed, safeArg)
        MessageCode.OrderRefundNeedReason -> stringResource(Res.string.order_refund_need_reason)
        MessageCode.OrderRefundSuccess -> stringResource(Res.string.order_refund_success)
        MessageCode.OrderRefundSubmitFailed -> stringResource(Res.string.order_refund_submit_failed, safeArg)
        MessageCode.CsCreateSessionFailed -> stringResource(Res.string.cs_create_session_failed)
        MessageCode.CsLoadMessagesFailed -> stringResource(Res.string.cs_load_messages_failed)
        MessageCode.CsSendFailed -> stringResource(Res.string.cs_send_failed)
        MessageCode.CsUploadFailed -> stringResource(Res.string.cs_upload_failed)
        MessageCode.CsSessionNotReady -> stringResource(Res.string.cs_session_not_ready)
        MessageCode.CsConnectingRetry -> stringResource(Res.string.cs_connecting_retry)
        MessageCode.FeedbackNeedType -> stringResource(Res.string.feedback_need_type)
        MessageCode.FeedbackNeedContent -> stringResource(Res.string.feedback_need_content)
        MessageCode.FeedbackSubmitSuccess -> stringResource(Res.string.feedback_submit_success)
        MessageCode.FeedbackSubmitFailed -> stringResource(Res.string.feedback_submit_failed, safeArg)
        MessageCode.FeedbackTypeLoadFailed -> stringResource(Res.string.feedback_type_load_failed)
        MessageCode.FeedbackMaxImages -> stringResource(Res.string.feedback_max_images, intArg)
        MessageCode.DocEmpty -> stringResource(Res.string.doc_empty)
        MessageCode.CouponReceiveSuccess -> stringResource(Res.string.coupon_receive_success)
    }
}

/**
 * 非 Composable 协程中解析（如 Snackbar collect）。
 */
suspend fun resolveUiMessageSuspend(message: UiMessage): String = when (message) {
    is UiMessage.Raw -> message.text
    is UiMessage.Code -> resolveMessageCodeSuspend(message.code, message.arg)
}

suspend fun resolveMessageCodeSuspend(code: MessageCode, arg: String? = null): String {
    val safeArg = arg.orEmpty()
    val intArg = arg?.toIntOrNull() ?: 0
    return when (code) {
        MessageCode.InvalidPhone -> getString(Res.string.auth_err_invalid_phone)
        MessageCode.InvalidSmsCode -> getString(Res.string.auth_err_invalid_sms_code)
        MessageCode.PasswordTooShort -> getString(Res.string.auth_err_password_too_short)
        MessageCode.PasswordMismatch -> getString(Res.string.auth_err_password_mismatch)
        MessageCode.LoginFailed -> getString(Res.string.auth_err_login_failed, safeArg)
        MessageCode.RegisterFailed -> getString(Res.string.auth_err_register_failed, safeArg)
        MessageCode.SendCodeFailed -> getString(Res.string.auth_err_send_code_failed, safeArg)
        MessageCode.NetworkError -> getString(Res.string.common_network_error)
        MessageCode.ThirdPartyNotConfigured -> getString(Res.string.auth_third_party_unavailable)
        MessageCode.CodeSent -> getString(Res.string.auth_code_sent)
        MessageCode.ResetPasswordPending -> getString(Res.string.auth_reset_pending)
        MessageCode.ResetPasswordTip -> getString(Res.string.auth_reset_tip)
        MessageCode.AddressContactRequired -> getString(Res.string.address_err_contact_required)
        MessageCode.AddressPhoneRequired -> getString(Res.string.address_err_phone_required)
        MessageCode.AddressPhoneInvalid -> getString(Res.string.address_err_phone_invalid)
        MessageCode.AddressRegionRequired -> getString(Res.string.address_err_region_required)
        MessageCode.AddressDetailRequired -> getString(Res.string.address_err_detail_required)
        MessageCode.AddressLoadFailed -> getString(Res.string.address_err_load_failed, safeArg)
        MessageCode.AddressSaveFailed -> getString(Res.string.address_err_save_failed, safeArg)
        MessageCode.AddressSaved -> getString(Res.string.address_msg_saved)
        MessageCode.AddressAdded -> getString(Res.string.address_msg_added)
        MessageCode.AddressDeleted -> getString(Res.string.address_deleted)
        MessageCode.AddressDeleteFailed -> getString(Res.string.address_delete_failed, safeArg)
        MessageCode.AddressSetDefaultOk -> getString(Res.string.address_set_default_ok)
        MessageCode.AddressSetDefaultFailed -> getString(Res.string.address_set_default_failed, safeArg)
        MessageCode.LoadFailed -> getString(Res.string.common_load_failed, safeArg)
        MessageCode.SpecsLoadFailed -> getString(Res.string.goods_err_specs_load_failed, safeArg)
        MessageCode.SelectSpecRequired -> getString(Res.string.goods_err_select_spec)
        MessageCode.InvalidQuantity -> getString(Res.string.goods_err_invalid_quantity)
        MessageCode.AddedToCart -> getString(Res.string.goods_msg_added_to_cart)
        MessageCode.FootprintCleared -> getString(Res.string.footprint_cleared)
        MessageCode.FootprintClearFailed -> getString(Res.string.footprint_clear_failed)
        MessageCode.FootprintDeleteFailed -> getString(Res.string.footprint_delete_failed)
        MessageCode.OrderCancelled -> getString(Res.string.order_cancelled)
        MessageCode.OrderCancelFailed -> getString(Res.string.order_cancel_failed, safeArg)
        MessageCode.OrderCancelReasonUser -> getString(Res.string.order_cancel_reason_user)
        MessageCode.OrderConfirmedReceive -> getString(Res.string.order_confirmed_receive)
        MessageCode.OrderConfirmFailed -> getString(Res.string.order_confirm_failed, safeArg)
        MessageCode.OrderCommentNoGoods -> getString(Res.string.order_comment_no_goods)
        MessageCode.OrderCommentNeedRating -> getString(Res.string.order_comment_need_rating)
        MessageCode.OrderCommentNeedContent -> getString(Res.string.order_comment_need_content)
        MessageCode.OrderCommentSuccess -> getString(Res.string.order_comment_success)
        MessageCode.OrderCommentSubmitFailed -> getString(Res.string.order_comment_submit_failed, safeArg)
        MessageCode.OrderCommentPicUrlRequired -> getString(Res.string.order_comment_pic_url_required)
        MessageCode.OrderCommentPicUrlInvalid -> getString(Res.string.order_comment_pic_url_invalid)
        MessageCode.OrderCommentPicMax -> getString(Res.string.order_comment_pic_max, intArg)
        MessageCode.OrderCommentPicDup -> getString(Res.string.order_comment_pic_dup)
        MessageCode.OrderCommentPicUploadFailed -> getString(Res.string.order_comment_pic_upload_failed)
        MessageCode.OrderConfirmNeedAddress -> getString(Res.string.order_confirm_need_address)
        MessageCode.OrderConfirmNoGoods -> getString(Res.string.order_confirm_no_goods)
        MessageCode.OrderConfirmTitleGoods -> getString(Res.string.order_confirm_title_goods)
        MessageCode.OrderCreateFailed -> getString(Res.string.order_create_failed, safeArg)
        MessageCode.OrderRefundNeedReason -> getString(Res.string.order_refund_need_reason)
        MessageCode.OrderRefundSuccess -> getString(Res.string.order_refund_success)
        MessageCode.OrderRefundSubmitFailed -> getString(Res.string.order_refund_submit_failed, safeArg)
        MessageCode.CsCreateSessionFailed -> getString(Res.string.cs_create_session_failed)
        MessageCode.CsLoadMessagesFailed -> getString(Res.string.cs_load_messages_failed)
        MessageCode.CsSendFailed -> getString(Res.string.cs_send_failed)
        MessageCode.CsUploadFailed -> getString(Res.string.cs_upload_failed)
        MessageCode.CsSessionNotReady -> getString(Res.string.cs_session_not_ready)
        MessageCode.CsConnectingRetry -> getString(Res.string.cs_connecting_retry)
        MessageCode.FeedbackNeedType -> getString(Res.string.feedback_need_type)
        MessageCode.FeedbackNeedContent -> getString(Res.string.feedback_need_content)
        MessageCode.FeedbackSubmitSuccess -> getString(Res.string.feedback_submit_success)
        MessageCode.FeedbackSubmitFailed -> getString(Res.string.feedback_submit_failed, safeArg)
        MessageCode.FeedbackTypeLoadFailed -> getString(Res.string.feedback_type_load_failed)
        MessageCode.FeedbackMaxImages -> getString(Res.string.feedback_max_images, intArg)
        MessageCode.DocEmpty -> getString(Res.string.doc_empty)
        MessageCode.CouponReceiveSuccess -> getString(Res.string.coupon_receive_success)
    }
}
