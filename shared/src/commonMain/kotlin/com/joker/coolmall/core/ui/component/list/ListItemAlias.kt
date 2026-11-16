package com.joker.coolmall.core.ui.component.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joker.coolmall.core.ui.component.ListItem as SharedListItem

@Composable
fun AppListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    showArrow: Boolean = true,
    showDivider: Boolean = true,
    onClick: () -> Unit = {},
) = SharedListItem(
    title = title,
    modifier = modifier,
    description = description,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
    showArrow = showArrow,
    showDivider = showDivider,
    onClick = onClick,
)
