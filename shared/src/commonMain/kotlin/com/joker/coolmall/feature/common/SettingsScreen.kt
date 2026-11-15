package com.joker.coolmall.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.data.local.ThemeColor
import com.joker.coolmall.core.data.local.ThemeMode
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.ui.component.TitleWithLine
import com.joker.coolmall.core.platform.clearAppCache
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_cancel
import myapplication.shared.generated.resources.common_confirm
import myapplication.shared.generated.resources.privacy_policy
import myapplication.shared.generated.resources.me_about
import myapplication.shared.generated.resources.settings_account_security
import myapplication.shared.generated.resources.settings_app_guide
import myapplication.shared.generated.resources.settings_cache_size
import myapplication.shared.generated.resources.settings_clear_cache
import myapplication.shared.generated.resources.settings_dark_mode
import myapplication.shared.generated.resources.settings_feedback
import myapplication.shared.generated.resources.settings_hint
import myapplication.shared.generated.resources.settings_language
import myapplication.shared.generated.resources.settings_language_chinese
import myapplication.shared.generated.resources.settings_replay_guide
import myapplication.shared.generated.resources.settings_section_appearance
import myapplication.shared.generated.resources.settings_section_general
import myapplication.shared.generated.resources.settings_section_other
import myapplication.shared.generated.resources.settings_section_privacy
import myapplication.shared.generated.resources.settings_select_theme
import myapplication.shared.generated.resources.settings_select_theme_color
import myapplication.shared.generated.resources.settings_show_splash
import myapplication.shared.generated.resources.settings_show_splash_desc
import myapplication.shared.generated.resources.settings_theme_color
import myapplication.shared.generated.resources.settings_theme_color_default
import myapplication.shared.generated.resources.settings_theme_color_green
import myapplication.shared.generated.resources.settings_theme_color_orange
import myapplication.shared.generated.resources.settings_theme_color_purple
import myapplication.shared.generated.resources.settings_theme_color_red
import myapplication.shared.generated.resources.settings_theme_dark
import myapplication.shared.generated.resources.settings_theme_light
import myapplication.shared.generated.resources.settings_theme_system
import myapplication.shared.generated.resources.settings_title
import myapplication.shared.generated.resources.user_agreement
import org.jetbrains.compose.resources.stringResource

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
        },
    ),
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }
    var showCacheDialog by remember { mutableStateOf(false) }
    val showSplash by viewModel.showSplash.collectAsState()

    if (showThemeDialog) {
        ThemeModeDialog(
            selected = themeMode,
            onDismiss = { showThemeDialog = false },
            onSelect = {
                viewModel.setThemeMode(it)
                showThemeDialog = false
            },
        )
    }
    if (showColorDialog) {
        ThemeColorDialog(
            selected = themeColor,
            onDismiss = { showColorDialog = false },
            onSelect = {
                viewModel.setThemeColor(it)
                showColorDialog = false
            },
        )
    }
    if (showCacheDialog) {
        AlertDialog(
            onDismissRequest = { showCacheDialog = false },
            title = { Text(stringResource(Res.string.settings_clear_cache)) },
            text = { Text(stringResource(Res.string.settings_cache_size)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        clearAppCache()
                        showCacheDialog = false
                    },
                ) {
                    Text(stringResource(Res.string.common_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCacheDialog = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                )
                SettingsValueRow(
                    title = stringResource(Res.string.settings_language),
                    value = stringResource(Res.string.settings_language_chinese),
                    showDivider = false,
                )
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
                    title = stringResource(Res.string.settings_show_splash),
                    description = stringResource(Res.string.settings_show_splash_desc),
                    checked = showSplash,
                    onCheckedChange = viewModel::setShowSplash,
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
                    onClick = { showCacheDialog = true },
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
            Text(
                text = stringResource(Res.string.settings_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    TitleWithLine(
        text = text,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 2.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
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
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            value?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.size(8.dp))
            }
            trailing?.invoke()
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (showDivider) {
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun SettingsValueRow(title: String, value: String, showDivider: Boolean = true) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (showDivider) {
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun ThemeModeDialog(
    selected: ThemeMode,
    onDismiss: () -> Unit,
    onSelect: (ThemeMode) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_select_theme)) },
        text = {
            Column {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier.fillMaxWidth().selectable(
                            selected = selected == mode,
                            onClick = { onSelect(mode) },
                            role = Role.RadioButton,
                        ).padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected == mode, onClick = null)
                        Spacer(Modifier.size(8.dp))
                        Text(themeModeLabel(mode))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun ThemeColorDialog(
    selected: ThemeColor,
    onDismiss: () -> Unit,
    onSelect: (ThemeColor) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_select_theme_color)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeColor.entries.forEach { color ->
                    FilterChip(
                        selected = selected == color,
                        onClick = { onSelect(color) },
                        label = { Text(themeColorLabel(color)) },
                        leadingIcon = { ThemeColorDot(color) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
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
    ThemeColor.DEFAULT -> Color(0xFF9B82D9)
    ThemeColor.PURPLE -> Color(0xFF7E57C2)
    ThemeColor.GREEN -> Color(0xFF43A047)
    ThemeColor.ORANGE -> Color(0xFFFB8C00)
    ThemeColor.RED -> Color(0xFFE53935)
}
