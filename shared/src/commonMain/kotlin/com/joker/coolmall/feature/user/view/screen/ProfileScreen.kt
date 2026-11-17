package com.joker.coolmallkmp.feature.user.view.screen
import com.joker.coolmallkmp.feature.user.viewmodel.ProfileViewModel
import com.joker.coolmallkmp.feature.user.viewmodel.*

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.rememberTopAppBarState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.model.entity.User
import com.joker.coolmallkmp.core.ui.component.TitleWithLine
import com.joker.coolmallkmp.core.ui.component.AppDivider
import com.joker.coolmallkmp.core.ui.resolveUiMessageSuspend
import com.joker.coolmallkmp.core.ui.theme.ColorDanger
import com.joker.coolmallkmp.core.ui.theme.SpaceHorizontalLarge
import com.joker.coolmallkmp.core.ui.theme.SpaceVerticalLarge
import com.joker.coolmallkmp.core.ui.theme.SpaceVerticalMedium
import com.joker.coolmallkmp.core.ui.theme.SpaceVerticalSmall
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.common_back
import coolmallkmp.shared.generated.resources.common_cancel
import coolmallkmp.shared.generated.resources.common_save
import coolmallkmp.shared.generated.resources.me_logout
import coolmallkmp.shared.generated.resources.me_user_fallback
import coolmallkmp.shared.generated.resources.profile_account
import coolmallkmp.shared.generated.resources.profile_account_info
import coolmallkmp.shared.generated.resources.profile_avatar
import coolmallkmp.shared.generated.resources.profile_nickname
import coolmallkmp.shared.generated.resources.profile_not_bound
import coolmallkmp.shared.generated.resources.profile_not_supported
import coolmallkmp.shared.generated.resources.profile_not_set
import coolmallkmp.shared.generated.resources.profile_phone
import coolmallkmp.shared.generated.resources.profile_title
import coolmallkmp.shared.generated.resources.profile_social_accounts
import coolmallkmp.shared.generated.resources.profile_wechat
import coolmallkmp.shared.generated.resources.profile_qq
import coolmallkmp.shared.generated.resources.profile_security
import coolmallkmp.shared.generated.resources.profile_change_password
import coolmallkmp.shared.generated.resources.profile_confirm_password
import coolmallkmp.shared.generated.resources.profile_current_password
import coolmallkmp.shared.generated.resources.profile_delete_account
import coolmallkmp.shared.generated.resources.profile_delete_confirm
import coolmallkmp.shared.generated.resources.profile_delete_confirm_button
import coolmallkmp.shared.generated.resources.profile_new_password
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(resolveUiMessageSuspend(it)) }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    val scrollFraction = scrollBehavior.state.collapsedFraction
                    Text(
                        text = stringResource(Res.string.profile_title),
                        fontSize = lerp(30.sp, 16.sp, scrollFraction),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        ProfileContent(
            user = user,
            isLoggedIn = isLoggedIn,
            onLogoutClick = {
                viewModel.logout(onCompleted = onLoggedOut)
            },
            onChangePasswordClick = { showChangePassword = true },
            onDeleteAccountClick = { showDeleteAccount = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = SpaceVerticalMedium),
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
            title = { Text(stringResource(Res.string.profile_delete_account)) },
            text = { Text(stringResource(Res.string.profile_delete_confirm)) },
            confirmButton = {
                Button(onClick = {
                    showDeleteAccount = false
                    viewModel.deleteAccount(onLoggedOut)
                }) { Text(stringResource(Res.string.profile_delete_confirm_button)) }
            },
            dismissButton = { Button(onClick = { showDeleteAccount = false }) {
                Text(stringResource(Res.string.common_cancel))
            } },
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
                verticalPadding = SpaceVerticalSmall,
            )
            ProfileRow(
                title = stringResource(Res.string.profile_nickname),
                value = user?.nickName?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.profile_not_set),
                showDivider = false,
            )
        }

        TitleWithLine(
            text = stringResource(Res.string.profile_account_info),
            modifier = Modifier.padding(top = SpaceVerticalSmall),
        )

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

        TitleWithLine(
            text = stringResource(Res.string.profile_social_accounts),
            modifier = Modifier.padding(top = SpaceVerticalSmall),
        )
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

        TitleWithLine(
            text = stringResource(Res.string.profile_security),
            modifier = Modifier.padding(top = SpaceVerticalSmall),
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            ProfileRow(
                title = stringResource(Res.string.profile_change_password),
                onClick = onChangePasswordClick,
                showDivider = true,
                showArrow = true,
            )
            ProfileRow(
                title = stringResource(Res.string.profile_delete_account),
                onClick = onDeleteAccountClick,
                showDivider = false,
                showArrow = true,
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
    showArrow: Boolean = false,
    verticalPadding: Dp = SpaceVerticalLarge,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = SpaceHorizontalLarge, vertical = verticalPadding),
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
            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        if (showDivider) {
            AppDivider(
                modifier = Modifier.padding(start = SpaceHorizontalLarge),
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
        title = { Text(stringResource(Res.string.profile_change_password)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.material3.OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text(stringResource(Res.string.profile_current_password)) },
                )
                androidx.compose.material3.OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(Res.string.profile_new_password)) },
                )
                androidx.compose.material3.OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(Res.string.profile_confirm_password)) },
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(oldPassword, newPassword, confirmPassword) }) {
                Text(stringResource(Res.string.common_save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(Res.string.common_cancel))
            }
        },
    )
}
