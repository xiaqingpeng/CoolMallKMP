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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import myapplication.shared.generated.resources.profile_not_set
import myapplication.shared.generated.resources.profile_phone
import myapplication.shared.generated.resources.profile_title
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

    Scaffold(
        topBar = {
            TopAppBar(
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
        ProfileContent(
            user = user,
            isLoggedIn = isLoggedIn,
            onLogoutClick = {
                viewModel.logout()
                onLoggedOut()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpaceHorizontalLarge, vertical = SpaceVerticalMedium),
        )
    }
}

@Composable
private fun ProfileContent(
    user: User?,
    isLoggedIn: Boolean,
    onLogoutClick: () -> Unit,
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
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
