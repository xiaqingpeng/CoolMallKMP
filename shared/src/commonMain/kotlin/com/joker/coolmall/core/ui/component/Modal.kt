package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class ModalType { BOTTOM, CENTER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modal(
    visible: Boolean,
    modalType: ModalType = ModalType.BOTTOM,
    onDismiss: () -> Unit,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    indicatorColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
    horizontalPadding: Dp = 0.dp,
    skipPartiallyExpanded: Boolean = true,
    dragHandleVerticalPadding: Dp = 12.dp,
    bottomContentPadding: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return
    when (modalType) {
        ModalType.BOTTOM -> {
            val sheetState = rememberBottomSheetState(
                initialValue = SheetValue.Hidden,
                confirmValueChange = { target ->
                    !skipPartiallyExpanded || target != SheetValue.PartiallyExpanded
                },
            )
            LaunchedEffect(Unit) {
                sheetState.expand()
            }
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                containerColor = containerColor,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dragHandleVerticalPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp, 4.dp)
                                .background(indicatorColor, RoundedCornerShape(2.dp)),
                        )
                    }
                },
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = horizontalPadding),
                ) {
                    content()
                    if (bottomContentPadding > 0.dp) {
                        Spacer(Modifier.height(bottomContentPadding))
                    }
                }
            }
        }
        ModalType.CENTER -> Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
            ),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                content = content,
            )
        }
    }
}
