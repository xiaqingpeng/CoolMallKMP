package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String = "",
    modifier: Modifier = Modifier,
    showBack: Boolean = true,
    onBack: () -> Unit = {},
    actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    useLargeTopBar: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior = if (useLargeTopBar) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    } else {
        null
    }
    Scaffold(
        modifier = if (scrollBehavior != null) {
            modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            modifier.fillMaxSize()
        },
        topBar = {
            if (useLargeTopBar) {
                LargeTopAppBar(
                    title = title,
                    onBack = onBack,
                    showBack = showBack,
                    actions = actions,
                    scrollBehavior = scrollBehavior,
                )
            } else {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (showBack) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                            }
                        }
                    },
                    actions = actions,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                )
            }
        },
        bottomBar = bottomBar,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LargeTopAppBar(
    title: String,
    onBack: () -> Unit,
    showBack: Boolean,
    actions: @Composable RowScope.() -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
) {
    val collapsedFraction = scrollBehavior?.state?.collapsedFraction ?: 0f
    MediumTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = lerp(30.sp, 16.sp, collapsedFraction),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(),
    )
}
