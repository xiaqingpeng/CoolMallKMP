package com.joker.coolmallkmp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.ui.designsystem.AppBox
import com.joker.coolmallkmp.core.ui.designsystem.AppColumn
import com.joker.coolmallkmp.core.ui.designsystem.AppLazyColumn
import com.joker.coolmallkmp.core.ui.designsystem.AppLazyRow
import com.joker.coolmallkmp.core.ui.designsystem.AppRow

@Composable
fun AppBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    fillMaxSize: Boolean = false,
    fillMaxWidth: Boolean = true,
    padding: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit,
) = com.joker.coolmallkmp.core.ui.designsystem.AppBox(
    modifier, contentAlignment, fillMaxSize, fillMaxWidth, padding, content
)

@Composable
fun AppColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    fillMaxWidth: Boolean = true,
    padding: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit,
) = com.joker.coolmallkmp.core.ui.designsystem.AppColumn(
    modifier, verticalArrangement, horizontalAlignment, fillMaxWidth, padding, content
)

@Composable
fun AppRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    fillMaxWidth: Boolean = true,
    padding: Dp = 0.dp,
    content: @Composable RowScope.() -> Unit,
) = com.joker.coolmallkmp.core.ui.designsystem.AppRow(
    modifier, horizontalArrangement, verticalAlignment, fillMaxWidth, padding, content
)

@Composable
fun AppLazyColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    fillMaxSize: Boolean = false,
    content: LazyListScope.() -> Unit,
) = com.joker.coolmallkmp.core.ui.designsystem.AppLazyColumn(
    modifier = modifier,
    contentPadding = contentPadding,
    verticalArrangement = verticalArrangement,
    fillMaxSize = fillMaxSize,
    content = content,
)

@Composable
fun AppLazyRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: LazyListScope.() -> Unit,
) = com.joker.coolmallkmp.core.ui.designsystem.AppLazyRow(modifier, contentPadding, content)
