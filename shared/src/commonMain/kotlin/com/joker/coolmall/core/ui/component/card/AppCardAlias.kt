package com.joker.coolmall.core.ui.component.card

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joker.coolmall.core.ui.component.AppCard as SharedAppCard

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    lineTitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) = SharedAppCard(modifier, title, lineTitle, content)
