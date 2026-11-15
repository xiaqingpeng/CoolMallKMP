package com.joker.coolmall.feature.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.core.ui.component.TitleWithLine
import com.joker.coolmall.core.ui.resolveUiMessageSuspend
import com.joker.coolmall.core.ui.theme.ColorDanger
import com.joker.coolmall.core.ui.theme.SpaceHorizontalLarge
import com.joker.coolmall.core.ui.theme.SpaceVerticalLarge
import com.joker.coolmall.core.ui.theme.SpaceVerticalMedium
import com.joker.coolmall.core.ui.theme.SpaceVerticalSmall
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.me_logout
import myapplication.shared.generated.resources.me_user_fallback
import myapplication.shared.generated.resources.profile_account
import myapplication.shared.generated.resources.profile_account_info
import myapplication.shared.generated.resources.profile_avatar
import myapplication.shared.generated.resources.profile_nickname
import myapplication.shared.generated.resources.profile_not_bound
import myapplication.shared.generated.resources.profile_not_supported
import myapplication.shared.generated.resources.profile_not_set
import myapplication.shared.generated.resources.profile_phone
import myapplication.shared.generated.resources.profile_title
import myapplication.shared.generated.resources.profile_social_accounts
import myapplication.shared.generated.resources.profile_wechat
import myapplication.shared.generated.resources.profile_qq
import myapplication.shared.generated.resources.profile_security
import myapplication.shared.generated.resources.profile_change_password
import myapplication.shared.generated.resources.profile_delete_account
import org.jetbrains.compose.resources.stringResource

/**
 * 个人资料路由（只读展示 + 退出登录；iOS 不做 QQ/微信绑定区）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit = onBack,
    viewModel: ProfileViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val c = AppContainerProvider.instance
                ProfileViewModel(
                    authRepository = c.authRepository,
                    userRepository = c.userRepository,
                )
            }
        }
    ),
) {
    val user by viewModel.user.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var showChangePassword by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(Res.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        androidx.compose.material3.SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxSize().padding(padding),
        )
        ProfileContent(
            user = user,
            isLoggedIn = isLoggedIn,
            onLogoutClick = {
                viewModel.logout()
                onLoggedOut()
            },
            onChangePasswordClick = { showChangePassword = true },
            onDeleteAccountClick = { showDeleteAccount = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpaceHorizontalLarge, vertical = SpaceVerticalMedium),
        )
    }
    if (showChangePassword) {
        ChangePasswordDialog(
            onDismiss = { showChangePassword = false },
            onConfirm = { old, new, confirm ->
                showChangePassword = false
                viewModel.changePassword(old, new, confirm)
            },
        )
    }
    if (showDeleteAccount) {
        AlertDialog(
            onDismissRequest = { showDeleteAccount = false },
            title = { Text("注销账号") },
            text = { Text("注销后账号及相关数据可能无法恢复，确定继续吗？") },
            confirmButton = {
                Button(onClick = {
                    showDeleteAccount = false
                    viewModel.deleteAccount(onLoggedOut)
                }) { Text("确认注销") }
            },
            dismissButton = {
                Button(onClick = { showDeleteAccount = false }) { Text("取消") }
            },
        )
    }
}

@Composable
private fun ProfileContent(
    user: User?,
    isLoggedIn: Boolean,
    onLogoutClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            ProfileRow(
                title = stringResource(Res.string.profile_avatar),
                trailing = {
                    ProfileAvatar(user = user, isLoggedIn = isLoggedIn)
                },
                showDivider = true,
            )
            ProfileRow(
                title = stringResource(Res.string.profile_nickname),
                value = user?.nickName?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.profile_not_set),
                showDivider = false,
            )
        }

        TitleWithLine(text = stringResource(Res.string.profile_account_info))

        Card(modifier = Modifier.fillMaxWidth()) {
            ProfileRow(
                title = stringResource(Res.string.profile_phone),
                value = user?.phone?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.profile_not_bound),
                showDivider = true,
            )
            ProfileRow(
                title = stringResource(Res.string.profile_account),
                value = user?.id?.takeIf { it > 0 }?.toString()
                    ?: stringResource(Res.string.profile_not_set),
                showDivider = false,
            )
        }

        TitleWithLine(text = stringResource(Res.string.profile_social_accounts))
        Card(modifier = Modifier.fillMaxWidth()) {
            ProfileRow(
                title = stringResource(Res.string.profile_wechat),
                value = stringResource(Res.string.profile_not_bound),
                showDivider = true,
            )
            ProfileRow(
                title = stringResource(Res.string.profile_qq),
                value = stringResource(Res.string.profile_not_bound),
                showDivider = false,
            )
        }

        TitleWithLine(text = stringResource(Res.string.profile_security))
        Card(modifier = Modifier.fillMaxWidth()) {
            ProfileRow(
                title = stringResource(Res.string.profile_change_password),
                onClick = onChangePasswordClick,
                showDivider = true,
            )
            ProfileRow(
                title = stringResource(Res.string.profile_delete_account),
                onClick = onDeleteAccountClick,
                showDivider = false,
            )
        }

        if (isLoggedIn) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogoutClick),
            ) {
                Text(
                    text = stringResource(Res.string.me_logout),
                    color = ColorDanger,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SpaceVerticalLarge),
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    user: User?,
    isLoggedIn: Boolean,
) {
    val avatar = user?.avatarUrl
    if (isLoggedIn && !avatar.isNullOrBlank()) {
        AsyncImage(
            model = avatar,
            contentDescription = user?.nickName,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = user?.nickName?.firstOrNull()?.toString()
                    ?: stringResource(Res.string.me_user_fallback).first().toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ProfileRow(
    title: String,
    value: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    showDivider: Boolean,
    onClick: (() -> Unit)? = null,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = SpaceHorizontalLarge, vertical = SpaceVerticalLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            if (trailing != null) {
                trailing()
            } else if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = SpaceHorizontalLarge),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("修改密码") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.material3.OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("当前密码") },
                )
                androidx.compose.material3.OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("新密码") },
                )
                androidx.compose.material3.OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认新密码") },
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(oldPassword, newPassword, confirmPassword) }) {
                Text("保存")
            }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("取消") } },
    )
}
