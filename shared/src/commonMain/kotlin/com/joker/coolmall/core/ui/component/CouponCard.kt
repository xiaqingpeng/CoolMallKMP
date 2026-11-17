package com.joker.coolmallkmp.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.model.entity.Coupon
import com.joker.coolmallkmp.core.ui.theme.GradientPrimaryEnd
import com.joker.coolmallkmp.core.ui.theme.GradientPrimaryStart
import com.joker.coolmallkmp.core.ui.theme.TextWhite
import com.joker.coolmallkmp.core.util.DateTimeUtils
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.coupon_conditions
import coolmallkmp.shared.generated.resources.coupon_expired
import coolmallkmp.shared.generated.resources.coupon_fallback_title
import coolmallkmp.shared.generated.resources.coupon_insufficient
import coolmallkmp.shared.generated.resources.coupon_receive
import coolmallkmp.shared.generated.resources.coupon_select
import coolmallkmp.shared.generated.resources.coupon_selected
import coolmallkmp.shared.generated.resources.coupon_usage_instructions
import coolmallkmp.shared.generated.resources.coupon_use
import coolmallkmp.shared.generated.resources.coupon_used
import coolmallkmp.shared.generated.resources.coupon_valid_until
import org.jetbrains.compose.resources.stringResource

/** 优惠券卡片模式 */
enum class CouponCardMode {
    RECEIVE,
    SELECT,
    VIEW,
}

private enum class CouponStatus {
    AVAILABLE,
    SELECTED,
    USED,
    EXPIRED,
    INSUFFICIENT,
}

/**
 * 共享优惠券卡片（对齐 CoolMallKotlin CouponCard 主结构）
 */
@Composable
fun CouponCard(
    coupon: Coupon,
    modifier: Modifier = Modifier,
    mode: CouponCardMode = CouponCardMode.VIEW,
    isSelected: Boolean = false,
    currentPrice: Double? = null,
    onActionClick: () -> Unit = {},
    showDescription: Boolean = true,
    expandable: Boolean = true,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val status = remember(coupon, mode, isSelected, currentPrice) {
        when {
            coupon.useStatus == 1 -> CouponStatus.USED
            coupon.useStatus == 2 || isCouponExpired(coupon.endTime) -> CouponStatus.EXPIRED
            mode == CouponCardMode.SELECT && currentPrice != null -> {
                val full = coupon.condition?.fullAmount
                when {
                    full != null && currentPrice < full -> CouponStatus.INSUFFICIENT
                    isSelected -> CouponStatus.SELECTED
                    else -> CouponStatus.AVAILABLE
                }
            }
            mode == CouponCardMode.SELECT && isSelected -> CouponStatus.SELECTED
            else -> CouponStatus.AVAILABLE
        }
    }
    val unavailable = status == CouponStatus.USED ||
        status == CouponStatus.EXPIRED ||
        status == CouponStatus.INSUFFICIENT

    Card(modifier = modifier.fillMaxWidth()) {
        Box {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(GradientPrimaryStart, GradientPrimaryEnd),
                                ),
                                shape = RoundedCornerShape(8.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalOffer,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = coupon.title.ifBlank {
                                stringResource(Res.string.coupon_fallback_title)
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        coupon.endTime?.takeIf { it.isNotBlank() }?.let { end ->
                            Text(
                                text = stringResource(Res.string.coupon_valid_until, end),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        PriceText(
                            price = coupon.amount.toInt(),
                            showDecimals = false,
                            integerStyle = MaterialTheme.typography.titleLarge,
                        )
                        coupon.condition?.let { condition ->
                            Text(
                                text = stringResource(
                                    Res.string.coupon_conditions,
                                    condition.fullAmount.toInt(),
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                if (showDescription || expandable) {
                    AppDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val description = coupon.description
                            if (expandable && description.isNotBlank()) {
                                Row(
                                    modifier = Modifier.clickable { isExpanded = !isExpanded },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = stringResource(Res.string.coupon_usage_instructions),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) {
                                            Icons.Filled.KeyboardArrowUp
                                        } else {
                                            Icons.Filled.KeyboardArrowDown
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically(),
                                    exit = shrinkVertically(),
                                ) {
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp),
                                    )
                                }
                            } else if (showDescription && description.isNotBlank()) {
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        CouponActionButton(
                            status = status,
                            mode = mode,
                            enabled = !unavailable || status == CouponStatus.SELECTED,
                            onClick = onActionClick,
                        )
                    }
                }
            }

            if (unavailable) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                )
            }
        }
    }
}

@Composable
private fun CouponActionButton(
    status: CouponStatus,
    mode: CouponCardMode,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val label = when (status) {
        CouponStatus.USED -> stringResource(Res.string.coupon_used)
        CouponStatus.EXPIRED -> stringResource(Res.string.coupon_expired)
        CouponStatus.INSUFFICIENT -> stringResource(Res.string.coupon_insufficient)
        CouponStatus.SELECTED -> stringResource(Res.string.coupon_selected)
        CouponStatus.AVAILABLE -> when (mode) {
            CouponCardMode.RECEIVE -> stringResource(Res.string.coupon_receive)
            CouponCardMode.SELECT -> stringResource(Res.string.coupon_select)
            CouponCardMode.VIEW -> stringResource(Res.string.coupon_use)
        }
    }
    val clickable = enabled && status == CouponStatus.AVAILABLE
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (clickable) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .then(if (clickable) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

/**
 * 本地 endTime 二次过期判定（与 `useStatus == 2` 并行）。
 * 通过 [DateTimeUtils] 用 epoch 桥接，避免 kotlin.time / kotlinx.datetime Instant 冲突。
 */
private fun isCouponExpired(endTime: String?): Boolean =
    DateTimeUtils.isDateExpired(endTime)
