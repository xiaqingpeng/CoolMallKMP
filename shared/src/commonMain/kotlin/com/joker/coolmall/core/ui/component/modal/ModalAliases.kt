package com.joker.coolmallkmp.core.ui.component.modal

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.joker.coolmallkmp.core.ui.component.Modal as SharedModal
import com.joker.coolmallkmp.core.ui.component.ModalType as SharedModalType

typealias ModalType = SharedModalType

@Composable
fun Modal(
    visible: Boolean,
    modalType: ModalType = ModalType.BOTTOM,
    onDismiss: () -> Unit,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) = SharedModal(
    visible = visible,
    modalType = modalType,
    onDismiss = onDismiss,
    dismissOnBackPress = dismissOnBackPress,
    dismissOnClickOutside = dismissOnClickOutside,
    content = content,
)
