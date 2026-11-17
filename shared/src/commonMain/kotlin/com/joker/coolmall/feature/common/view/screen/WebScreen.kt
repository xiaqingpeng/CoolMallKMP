package com.joker.coolmallkmp.feature.common.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.joker.coolmallkmp.core.platform.openExternalUrl
import com.joker.coolmallkmp.feature.common.view.platform.PlatformWebPage
import com.joker.coolmallkmp.feature.main.component.CenterTopAppBar
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.web_menu_open_browser
import coolmallkmp.shared.generated.resources.web_menu_refresh
import coolmallkmp.shared.generated.resources.web_more_options
import org.jetbrains.compose.resources.stringResource

/**
 * 通用网页入口。具体 WebView/WKWebView 由平台 actual 承载。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebRoute(
    url: String,
    title: String = "",
    onBack: () -> Unit,
) {
    val hasFixedTitle = title.isNotBlank()
    var pageTitle by remember(title) { mutableStateOf(title) }
    var progress by remember { mutableStateOf(0) }
    var reloadToken by remember { mutableStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = pageTitle,
                showBackIcon = true,
                onBackClick = onBack,
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, stringResource(Res.string.web_more_options))
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.web_menu_refresh)) },
                                leadingIcon = { Icon(Icons.Default.Refresh, null) },
                                onClick = {
                                    reloadToken++
                                    menuExpanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.web_menu_open_browser)) },
                                leadingIcon = { Icon(Icons.Default.OpenInBrowser, null) },
                                onClick = {
                                    openExternalUrl(url)
                                    menuExpanded = false
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            if (progress in 1..99) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            PlatformWebPage(
                url = url,
                reloadToken = reloadToken,
                onTitleChange = {
                    if (!hasFixedTitle && it.isNotBlank()) {
                        pageTitle = it
                    }
                },
                onProgressChange = { progress = it },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
