package com.joker.coolmallkmp.feature.common.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.feature.common.viewmodel.SettingsViewModel
import com.joker.coolmallkmp.core.data.local.ThemeColor
import com.joker.coolmallkmp.core.data.local.ThemeMode
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.core.ui.component.AppScaffold
import com.joker.coolmallkmp.core.ui.component.TitleWithLine
import com.joker.coolmallkmp.core.ui.component.AppListItem
import com.joker.coolmallkmp.core.ui.component.Modal
import com.joker.coolmallkmp.core.ui.component.ModalType
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.privacy_policy
import coolmallkmp.shared.generated.resources.me_about
import coolmallkmp.shared.generated.resources.settings_account_security
import coolmallkmp.shared.generated.resources.settings_app_guide
import coolmallkmp.shared.generated.resources.settings_cache_size
import coolmallkmp.shared.generated.resources.settings_clear_cache
import coolmallkmp.shared.generated.resources.settings_dark_mode
import coolmallkmp.shared.generated.resources.settings_feedback
import coolmallkmp.shared.generated.resources.settings_section_appearance
import coolmallkmp.shared.generated.resources.settings_section_general
import coolmallkmp.shared.generated.resources.settings_section_other
import coolmallkmp.shared.generated.resources.settings_section_privacy
import coolmallkmp.shared.generated.resources.settings_hide_splash
import coolmallkmp.shared.generated.resources.settings_hide_splash_desc
import coolmallkmp.shared.generated.resources.settings_theme_color
import coolmallkmp.shared.generated.resources.settings_theme_color_default
import coolmallkmp.shared.generated.resources.settings_theme_color_green
import coolmallkmp.shared.generated.resources.settings_theme_color_orange
import coolmallkmp.shared.generated.resources.settings_theme_color_purple
import coolmallkmp.shared.generated.resources.settings_theme_color_red
import coolmallkmp.shared.generated.resources.settings_theme_dark
import coolmallkmp.shared.generated.resources.settings_theme_light
import coolmallkmp.shared.generated.resources.settings_theme_system
import coolmallkmp.shared.generated.resources.settings_title
import coolmallkmp.shared.generated.resources.user_agreement
import org.jetbrains.compose.resources.stringResource
import wang.harlon.appsettings.language.languageItem
import wang.harlon.appsettings.ui.SettingsGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onAccountSecurityClick: () -> Unit = {},
    onReplayGuideClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    themePreferences = AppContainerProvider.instance.themePreferences,
                    splashPreferences = AppContainerProvider.instance.splashPreferences,
                )
            }
        }
    ),
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }
    val showSplash by viewModel.showSplash.collectAsState()

    if (showThemeDialog) {
        ThemeModeModal(
            selected = themeMode,
            onDismiss = { showThemeDialog = false },
            onSelect = {
                viewModel.setThemeMode(it)
                showThemeDialog = false
            },
        )
    }
    if (showColorDialog) {
        ThemeColorModal(
            selected = themeColor,
            onDismiss = { showColorDialog = false },
            onSelect = {
                viewModel.setThemeColor(it)
                showColorDialog = false
            },
        )
    }
    AppScaffold(
        title = stringResource(Res.string.settings_title),
        onBack = onBack,
        useLargeTopBar = true,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsCard {
                SettingsLinkRow(
                    title = stringResource(Res.string.settings_account_security),
                    onClick = onAccountSecurityClick,
                    showDivider = false,
                )
            }
            SettingsSectionTitle(stringResource(Res.string.settings_section_appearance))
            SettingsCard {
                SettingsLinkRow(
                    stringResource(Res.string.settings_dark_mode),
                    { showThemeDialog = true },
                    value = themeModeLabel(themeMode),
                )
                SettingsLinkRow(
                    stringResource(Res.string.settings_theme_color),
                    { showColorDialog = true },
                    trailing = { ThemeColorDot(themeColor) },
                    showDivider = false,
                )
            }
            SettingsGroup {
                languageItem()
            }
            SettingsSectionTitle(stringResource(Res.string.settings_section_privacy))
            SettingsCard {
                SettingsLinkRow(
                    title = stringResource(Res.string.privacy_policy),
                    onClick = onPrivacyPolicyClick,
                )
                SettingsLinkRow(
                    title = stringResource(Res.string.user_agreement),
                    onClick = onUserAgreementClick,
                    showDivider = false,
                )
            }
            SettingsSectionTitle(stringResource(Res.string.settings_section_other))
            SettingsCard {
                SettingsSwitchRow(
                    title = stringResource(Res.string.settings_hide_splash),
                    description = stringResource(Res.string.settings_hide_splash_desc),
                    checked = !showSplash,
                    onCheckedChange = { hidden -> viewModel.setShowSplash(!hidden) },
                )
                SettingsLinkRow(
                    title = stringResource(Res.string.settings_app_guide),
                    onClick = onReplayGuideClick,
                    showDivider = false,
                )
            }
            SettingsSectionTitle(stringResource(Res.string.settings_section_general))
            SettingsCard {
                SettingsLinkRow(
                    stringResource(Res.string.settings_clear_cache),
                    onClick = {},
                    value = stringResource(Res.string.settings_cache_size),
                )
                SettingsLinkRow(
                    title = stringResource(Res.string.settings_feedback),
                    onClick = onFeedbackClick,
                )
                SettingsLinkRow(
                    title = stringResource(Res.string.me_about),
                    onClick = onAboutClick,
                    showDivider = false,
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    TitleWithLine(
        text = text,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        content = content,
    )
}

@Composable
private fun SettingsLinkRow(
    title: String,
    onClick: () -> Unit,
    value: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    showDivider: Boolean = true,
) {
    AppListItem(
        title = title,
        horizontalPadding = 16.dp,
        verticalPadding = 16.dp,
        showDivider = showDivider,
        trailingContent = {
            value?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
            trailing?.invoke()
        },
        onClick = onClick,
    )
}

@Composable
private fun SettingsValueRow(title: String, value: String, showDivider: Boolean = true) {
    AppListItem(
        title = title,
        horizontalPadding = 16.dp,
        verticalPadding = 16.dp,
        showArrow = true,
        showDivider = showDivider,
        trailingContent = {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    AppListItem(
        title = title,
        description = description,
        horizontalPadding = 16.dp,
        verticalPadding = 10.dp,
        showArrow = false,
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
    )
}

@Composable
private fun ThemeModeModal(
    selected: ThemeMode,
    onDismiss: () -> Unit,
    onSelect: (ThemeMode) -> Unit,
) {
    Modal(
        visible = true,
        modalType = ModalType.BOTTOM,
        onDismiss = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        indicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        horizontalPadding = 16.dp,
        skipPartiallyExpanded = false,
        dragHandleVerticalPadding = 16.dp,
        bottomContentPadding = 12.dp,
    ) {
        Text(
            text = stringResource(Res.string.settings_dark_mode),
            style = MaterialTheme.typography.titleLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )
        Card {
            ThemeMode.entries.forEachIndexed { index, mode ->
                AppListItem(
                    title = themeModeLabel(mode),
                    showArrow = false,
                    horizontalPadding = 16.dp,
                    verticalPadding = 8.dp,
                    showDivider = index != ThemeMode.entries.lastIndex,
                    trailingContent = {
                        RadioButton(
                            selected = selected == mode,
                            onClick = { onSelect(mode) },
                        )
                    },
                    onClick = { onSelect(mode) },
                )
            }
        }
    }
}

@Composable
private fun ThemeColorModal(
    selected: ThemeColor,
    onDismiss: () -> Unit,
    onSelect: (ThemeColor) -> Unit,
) {
    Modal(
        visible = true,
        modalType = ModalType.BOTTOM,
        onDismiss = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        indicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        horizontalPadding = 16.dp,
        skipPartiallyExpanded = false,
        dragHandleVerticalPadding = 16.dp,
        bottomContentPadding = 12.dp,
    ) {
        Text(
            text = stringResource(Res.string.settings_theme_color),
            style = MaterialTheme.typography.titleLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )
        Card {
            ThemeColor.entries.forEachIndexed { index, color ->
                AppListItem(
                    title = themeColorLabel(color),
                    showArrow = false,
                    horizontalPadding = 16.dp,
                    verticalPadding = 8.dp,
                    showDivider = index != ThemeColor.entries.lastIndex,
                    trailingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            ThemeColorDot(color)
                            RadioButton(
                                selected = selected == color,
                                onClick = { onSelect(color) },
                            )
                        }
                    },
                    onClick = { onSelect(color) },
                )
            }
        }
    }
}

@Composable
private fun ThemeColorDot(color: ThemeColor) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(themeColorValue(color), CircleShape),
    )
}

@Composable
private fun themeModeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> stringResource(Res.string.settings_theme_system)
    ThemeMode.LIGHT -> stringResource(Res.string.settings_theme_light)
    ThemeMode.DARK -> stringResource(Res.string.settings_theme_dark)
}

@Composable
private fun themeColorLabel(color: ThemeColor): String = when (color) {
    ThemeColor.DEFAULT -> stringResource(Res.string.settings_theme_color_default)
    ThemeColor.PURPLE -> stringResource(Res.string.settings_theme_color_purple)
    ThemeColor.GREEN -> stringResource(Res.string.settings_theme_color_green)
    ThemeColor.ORANGE -> stringResource(Res.string.settings_theme_color_orange)
    ThemeColor.RED -> stringResource(Res.string.settings_theme_color_red)
}

private fun themeColorValue(color: ThemeColor): Color = when (color) {
    ThemeColor.DEFAULT -> Color(0xFF465CFF)
    ThemeColor.PURPLE -> Color(0xFF6831FF)
    ThemeColor.GREEN -> Color(0xFF09BE4F)
    ThemeColor.ORANGE -> Color(0xFFFFB703)
    ThemeColor.RED -> Color(0xFFFF2B2B)
}
