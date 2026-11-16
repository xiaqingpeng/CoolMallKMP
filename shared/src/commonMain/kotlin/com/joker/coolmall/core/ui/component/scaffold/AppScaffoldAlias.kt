package com.joker.coolmallkmp.core.ui.component.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joker.coolmallkmp.core.ui.component.AppScaffold as SharedAppScaffold

@Composable
fun AppScaffold(
    titleText: String? = null,
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = true,
    onBackClick: () -> Unit = {},
    topBarActions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) = SharedAppScaffold(
    title = titleText.orEmpty(),
    modifier = modifier,
    showBack = showBackIcon,
    onBack = onBackClick,
    actions = topBarActions,
    bottomBar = bottomBar,
    content = content,
)
