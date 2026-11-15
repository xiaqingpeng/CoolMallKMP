package com.joker.coolmall.feature.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.platform.openExternalUrl
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.web_menu_open_browser
import myapplication.shared.generated.resources.web_menu_refresh
import myapplication.shared.generated.resources.web_more_options
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
    var pageTitle by remember(title) { mutableStateOf(title) }
    var progress by remember { mutableStateOf(0) }
    var reloadToken by remember { mutableStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pageTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                },
                actions = {
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
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            if (progress in 1..99) {
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            PlatformWebPage(
                url = url,
                reloadToken = reloadToken,
                onTitleChange = { if (it.isNotBlank()) pageTitle = it },
                onProgressChange = { progress = it },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
