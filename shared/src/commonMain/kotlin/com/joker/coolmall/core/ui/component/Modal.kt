package com.joker.coolmallkmp.core.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.Alignment

enum class ModalType { BOTTOM, CENTER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modal(
    visible: Boolean,
    modalType: ModalType = ModalType.BOTTOM,
    onDismiss: () -> Unit,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    indicatorColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
    horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp,
    skipPartiallyExpanded: Boolean = true,
    dragHandleVerticalPadding: androidx.compose.ui.unit.Dp = 12.dp,
    bottomContentPadding: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return
    when (modalType) {
        ModalType.BOTTOM -> ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = skipPartiallyExpanded,
            ),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = containerColor,
            dragHandle = {
                Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(vertical = dragHandleVerticalPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = androidx.compose.ui.Modifier
                            .size(40.dp, 4.dp)
                            .background(
                                indicatorColor,
                                RoundedCornerShape(2.dp),
                            ),
                    )
                }
            },
        ) {
            Column(
                modifier = androidx.compose.ui.Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = horizontalPadding),
            ) {
                content()
                if (bottomContentPadding > 0.dp) {
                    androidx.compose.foundation.layout.Spacer(
                        androidx.compose.ui.Modifier.height(bottomContentPadding),
                    )
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
                modifier = androidx.compose.ui.Modifier.padding(horizontal = 24.dp),
                content = content,
            )
        }
    }
}
