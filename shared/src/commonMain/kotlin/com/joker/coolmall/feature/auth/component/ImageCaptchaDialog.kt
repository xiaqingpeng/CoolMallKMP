package com.joker.coolmallkmp.feature.auth.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joker.coolmallkmp.core.model.entity.Captcha
import coolmallkmp.shared.generated.resources.Res
import coolmallkmp.shared.generated.resources.auth_captcha_confirm
import coolmallkmp.shared.generated.resources.auth_captcha_title
import coolmallkmp.shared.generated.resources.auth_captcha_hint
import coolmallkmp.shared.generated.resources.common_cancel
import org.jetbrains.compose.resources.stringResource

@Composable
fun ImageCaptchaDialog(
    visible: Boolean,
    captcha: Captcha,
    imageCode: String,
    onImageCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onRefresh: () -> Unit,
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.auth_captcha_title)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = imageCode,
                        onValueChange = onImageCodeChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text(stringResource(Res.string.auth_captcha_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                    )
                    CaptchaImage(
                        captcha = captcha,
                        onRefresh = onRefresh,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = imageCode.length == 4,
            ) {
                Text(stringResource(Res.string.auth_captcha_confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(Res.string.common_cancel))
            }
        },
    )
}
