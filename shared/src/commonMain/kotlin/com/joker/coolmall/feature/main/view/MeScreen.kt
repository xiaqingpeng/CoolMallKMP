package com.joker.coolmall.feature.main.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.model.entity.Footprint
import com.joker.coolmall.core.model.entity.OrderCount
import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.core.ui.theme.MembershipCardBg
import com.joker.coolmall.core.ui.theme.MembershipGold
import com.joker.coolmall.core.ui.theme.PrimaryDefault
import com.joker.coolmall.core.ui.theme.SpaceHorizontalLarge
import com.joker.coolmall.core.ui.theme.SpaceHorizontalMedium
import com.joker.coolmall.core.ui.theme.SpaceHorizontalSmall
import com.joker.coolmall.core.ui.theme.SpacePaddingMedium
import com.joker.coolmall.core.ui.theme.SpaceVerticalLarge
import com.joker.coolmall.core.ui.theme.SpaceVerticalMedium
import com.joker.coolmall.core.ui.theme.SpaceVerticalSmall
import com.joker.coolmall.core.ui.theme.SpaceVerticalXSmall
import com.joker.coolmall.feature.main.component.CommonScaffold
import com.joker.coolmall.feature.main.viewmodel.MeViewModel
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.ic_coupon_fill
import myapplication.shared.generated.resources.ic_creative_fill
import myapplication.shared.generated.resources.ic_footprint_fill
import myapplication.shared.generated.resources.ic_location_fill
import myapplication.shared.generated.resources.ic_logistics
import myapplication.shared.generated.resources.ic_message
import myapplication.shared.generated.resources.ic_order_fill
import myapplication.shared.generated.resources.ic_pay
import myapplication.shared.generated.resources.ic_receipt
import myapplication.shared.generated.resources.ic_refund
import myapplication.shared.generated.resources.ic_service_fill
import myapplication.shared.generated.resources.ic_set_fill
import myapplication.shared.generated.resources.ic_tip_fill
import myapplication.shared.generated.resources.ic_vip_fill
import myapplication.shared.generated.resources.me_about
import myapplication.shared.generated.resources.me_address
import myapplication.shared.generated.resources.me_coupon
import myapplication.shared.generated.resources.me_customer_service
import myapplication.shared.generated.resources.me_feedback
import myapplication.shared.generated.resources.me_login_hint
import myapplication.shared.generated.resources.me_logged_in
import myapplication.shared.generated.resources.me_member
import myapplication.shared.generated.resources.me_member_activate
import myapplication.shared.generated.resources.me_member_benefits
import myapplication.shared.generated.resources.me_not_logged_in
import myapplication.shared.generated.resources.me_order_after_sale
import myapplication.shared.generated.resources.me_order_pending_comment
import myapplication.shared.generated.resources.me_order_pending_pay
import myapplication.shared.generated.resources.me_order_pending_receive
import myapplication.shared.generated.resources.me_order_pending_ship
import myapplication.shared.generated.resources.me_orders
import myapplication.shared.generated.resources.me_phone_prefix
import myapplication.shared.generated.resources.me_recent_footprint
import myapplication.shared.generated.resources.me_settings
import myapplication.shared.generated.resources.me_user_fallback
import myapplication.shared.generated.resources.me_view_all
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 「我的」页路由：回调导航 + MeViewModel（对齐 CoolMallKotlin 结构，去掉 Hilt/Navigator）
 */
@Composable
fun MeRoute(
    onLoginClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onOrderListClick: (Int) -> Unit = {},
    onCouponClick: () -> Unit = {},
    onFootprintClick: () -> Unit = {},
    onGoodsClick: (Long) -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCustomerServiceClick: () -> Unit = {},
    viewModel: MeViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val c = AppContainerProvider.instance
                MeViewModel(
                    authRepository = c.authRepository,
                    userRepository = c.userRepository,
                    orderRepository = c.orderRepository,
                    footprintRepository = c.footprintRepository,
                )
            }
        }
    ),
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val user by viewModel.user.collectAsState()
    val orderCount by viewModel.orderCount.collectAsState()
    val recentFootprints by viewModel.recentFootprints.collectAsState()

    var skipFirstResume by remember { mutableStateOf(true) }
    LifecycleResumeEffect(Unit) {
        if (skipFirstResume) {
            skipFirstResume = false
        } else {
            viewModel.onResume()
        }
        onPauseOrDispose { }
    }

    MeScreen(
        isLoggedIn = isLoggedIn,
        user = user,
        orderCount = orderCount,
        recentFootprints = recentFootprints,
        onLoginClick = onLoginClick,
        onProfileClick = onProfileClick,
        onAddressClick = onAddressClick,
        onOrderListClick = onOrderListClick,
        onCouponClick = onCouponClick,
        onFootprintClick = onFootprintClick,
        onGoodsClick = onGoodsClick,
        onFeedbackClick = onFeedbackClick,
        onAboutClick = onAboutClick,
        onSettingsClick = onSettingsClick,
        onCustomerServiceClick = onCustomerServiceClick,
    )
}

/**
 * 个人中心内容（对齐 CoolMallKotlin：用户信息 → 会员卡 → 订单 → 足迹 → 功能菜单）
 *
 * MainRoute 已 exclude statusBars；本页用 CommonScaffold（空 topBar）自行吃状态栏 inset。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeScreen(
    isLoggedIn: Boolean,
    user: User?,
    orderCount: OrderCount,
    recentFootprints: List<Footprint>,
    onLoginClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddressClick: () -> Unit,
    onOrderListClick: (Int) -> Unit,
    onCouponClick: () -> Unit,
    onFootprintClick: () -> Unit,
    onGoodsClick: (Long) -> Unit,
    onFeedbackClick: () -> Unit,
    onAboutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCustomerServiceClick: () -> Unit,
) {
    CommonScaffold(topBar = {}) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpaceHorizontalLarge),
            verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium),
        ) {
            Spacer(modifier = Modifier.height(SpaceVerticalMedium))

            UserInfoSection(
                isLoggedIn = isLoggedIn,
                user = user,
                onClick = {
                    if (isLoggedIn) onProfileClick() else onLoginClick()
                },
            )

            MembershipCard()

            OrderSection(
                orderCount = orderCount,
                onViewAll = { onOrderListClick(0) },
                onStatusClick = onOrderListClick,
            )

            if (recentFootprints.isNotEmpty()) {
                MyFootprintSection(
                    footprints = recentFootprints,
                    onViewAll = onFootprintClick,
                    onGoodsClick = onGoodsClick,
                )
            }

            FunctionMenuSection(
                onCouponClick = onCouponClick,
                onAddressClick = onAddressClick,
                onCustomerServiceClick = onCustomerServiceClick,
                onFeedbackClick = onFeedbackClick,
                onAboutClick = onAboutClick,
                onSettingsClick = onSettingsClick,
            )

            Spacer(modifier = Modifier.height(SpaceVerticalLarge))
        }
    }
}

/** 用户信息：72dp 头像 + 昵称/手机号 + 右箭头 */
@Composable
private fun UserInfoSection(
    isLoggedIn: Boolean,
    user: User?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val avatar = user?.avatarUrl
        if (isLoggedIn && user != null && !avatar.isNullOrBlank()) {
            AsyncImage(
                model = avatar,
                contentDescription = user.nickName,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isLoggedIn) {
                        user?.nickName?.firstOrNull()?.toString() ?: "我"
                    } else {
                        "?"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.width(SpaceHorizontalLarge))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isLoggedIn) {
                    user?.nickName?.takeIf { it.isNotBlank() }
                        ?: stringResource(Res.string.me_user_fallback)
                } else {
                    stringResource(Res.string.me_not_logged_in)
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(SpaceVerticalXSmall))
            Text(
                text = if (isLoggedIn) {
                    user?.phone?.takeIf { it.isNotBlank() }?.let {
                        stringResource(Res.string.me_phone_prefix, it)
                    } ?: stringResource(Res.string.me_logged_in)
                } else {
                    stringResource(Res.string.me_login_hint)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** 会员权益卡（展示-only，无开通后端） */
@Composable
private fun MembershipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MembershipCardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceHorizontalLarge, vertical = SpaceVerticalMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_vip_fill),
                contentDescription = stringResource(Res.string.me_member),
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(MembershipGold),
            )
            Spacer(modifier = Modifier.width(SpaceHorizontalSmall))
            Text(
                text = stringResource(Res.string.me_member_benefits),
                color = MembershipGold,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(Res.string.me_member_activate),
                color = MembershipGold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .border(1.dp, MembershipGold, RoundedCornerShape(20.dp))
                    .padding(horizontal = SpaceHorizontalMedium, vertical = 6.dp),
            )
        }
    }
}

/** 订单区：标题 + 五态图标角标 */
@Composable
private fun OrderSection(
    orderCount: OrderCount,
    onViewAll: () -> Unit,
    onStatusClick: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            MeListHeader(
                title = stringResource(Res.string.me_orders),
                trailingText = stringResource(Res.string.me_view_all),
                leadingIcon = Res.drawable.ic_order_fill,
                leadingTint = PrimaryDefault,
                onClick = onViewAll,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SpaceVerticalSmall),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OrderStatusItem(
                    icon = Res.drawable.ic_pay,
                    label = stringResource(Res.string.me_order_pending_pay),
                    badgeCount = orderCount.pendingPayment,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusClick(1) },
                )
                OrderStatusItem(
                    icon = Res.drawable.ic_receipt,
                    label = stringResource(Res.string.me_order_pending_ship),
                    badgeCount = orderCount.pendingShipment,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusClick(2) },
                )
                OrderStatusItem(
                    icon = Res.drawable.ic_logistics,
                    label = stringResource(Res.string.me_order_pending_receive),
                    badgeCount = orderCount.pendingReceive,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusClick(3) },
                )
                OrderStatusItem(
                    icon = Res.drawable.ic_message,
                    label = stringResource(Res.string.me_order_pending_comment),
                    badgeCount = orderCount.pendingReview,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusClick(5) },
                )
                OrderStatusItem(
                    icon = Res.drawable.ic_refund,
                    label = stringResource(Res.string.me_order_after_sale),
                    badgeCount = orderCount.afterSale,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusClick(4) },
                )
            }
        }
    }
}

@Composable
private fun OrderStatusItem(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    label: String,
    badgeCount: Int = 0,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = SpaceVerticalMedium),
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Image(
                painter = painterResource(icon),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            )
            if (badgeCount > 0) {
                Text(
                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .offset(x = 8.dp, y = (-6).dp)
                        .background(
                            color = MaterialTheme.colorScheme.error,
                            shape = CircleShape,
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(SpaceVerticalSmall))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

/** 最近足迹：标题 + 100dp 缩略图横滑 */
@Composable
private fun MyFootprintSection(
    footprints: List<Footprint>,
    onViewAll: () -> Unit,
    onGoodsClick: (Long) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            MeListHeader(
                title = stringResource(Res.string.me_recent_footprint),
                trailingText = stringResource(Res.string.me_view_all),
                leadingIcon = Res.drawable.ic_footprint_fill,
                leadingTint = Color(0xFFFF9800),
                onClick = onViewAll,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(
                    start = SpacePaddingMedium,
                    end = SpacePaddingMedium,
                    bottom = SpacePaddingMedium,
                ),
            ) {
                items(footprints, key = { it.goodsId }) { footprint ->
                    FootprintItem(
                        footprint = footprint,
                        onClick = { onGoodsClick(footprint.goodsId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FootprintItem(
    footprint: Footprint,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = footprint.goodsMainPic,
            contentDescription = footprint.goodsName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
    }
}

/**
 * 功能菜单：主卡（优惠券/地址/客服/反馈/关于）+ 独立设置卡
 * 对齐 CoolMallKotlin FunctionMenuSection 顺序与彩色图标
 */
@Composable
private fun FunctionMenuSection(
    onCouponClick: () -> Unit,
    onAddressClick: () -> Unit,
    onCustomerServiceClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onAboutClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            MeMenuRow(
                title = stringResource(Res.string.me_coupon),
                icon = Res.drawable.ic_coupon_fill,
                iconTint = Color(0xFF6A9BE6),
                onClick = onCouponClick,
                showDivider = true,
            )
            MeMenuRow(
                title = stringResource(Res.string.me_address),
                icon = Res.drawable.ic_location_fill,
                iconTint = Color(0xFF66BB6A),
                onClick = onAddressClick,
                showDivider = true,
            )
            MeMenuRow(
                title = stringResource(Res.string.me_customer_service),
                icon = Res.drawable.ic_service_fill,
                iconTint = Color(0xFFF87C7B),
                onClick = onCustomerServiceClick,
                showDivider = true,
            )
            MeMenuRow(
                title = stringResource(Res.string.me_feedback),
                icon = Res.drawable.ic_creative_fill,
                iconTint = Color(0xFFF3AF76),
                onClick = onFeedbackClick,
                showDivider = true,
            )
            MeMenuRow(
                title = stringResource(Res.string.me_about),
                icon = Res.drawable.ic_tip_fill,
                iconTint = Color(0xFF9179F1),
                onClick = onAboutClick,
                showDivider = false,
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        MeMenuRow(
            title = stringResource(Res.string.me_settings),
            icon = Res.drawable.ic_set_fill,
            iconTint = Color(0xFF26A69A),
            onClick = onSettingsClick,
            showDivider = false,
        )
    }
}

@Composable
private fun MeListHeader(
    title: String,
    trailingText: String,
    leadingIcon: DrawableResource,
    leadingTint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = SpaceHorizontalLarge, vertical = SpaceVerticalMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(leadingIcon),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            colorFilter = ColorFilter.tint(leadingTint),
        )
        Spacer(modifier = Modifier.width(SpaceHorizontalMedium))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = trailingText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun MeMenuRow(
    title: String,
    icon: DrawableResource,
    iconTint: Color,
    onClick: () -> Unit,
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = SpaceHorizontalLarge, vertical = SpaceVerticalLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(iconTint),
            )
            Spacer(modifier = Modifier.width(SpaceHorizontalMedium))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 54.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )
        }
    }
}
