package com.joker.coolmall.feature.auth.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joker.coolmall.core.model.entity.Captcha
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.auth_captcha_refresh
import myapplication.shared.generated.resources.auth_captcha_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun CaptchaImage(
    captcha: Captcha,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bitmap = remember(captcha.data) {
        captcha.data.takeIf { it.isNotBlank() }?.let(::decodeCaptchaImage)
    }
    if (bitmap == null) {
        Box(
            modifier = modifier
                .height(40.dp)
                .width(120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onRefresh),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.auth_captcha_refresh),
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        Image(
            bitmap = bitmap,
            contentDescription = stringResource(Res.string.auth_captcha_description),
            contentScale = ContentScale.FillBounds,
            modifier = modifier
                .height(40.dp)
                .width(120.dp)
                .clickable(onClick = onRefresh),
        )
    }
}

private fun decodeCaptchaImage(value: String): ImageBitmap? {
    val encoded = value.substringAfter("base64,", value)
    val bytes = runCatching {
        @Suppress("DEPRECATION")
        decodeBase64(encoded)
    }.getOrNull() ?: return null
    return imageBitmapFromBytes(bytes)
}

private fun decodeBase64(value: String): ByteArray {
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    val clean = value.filterNot { it.isWhitespace() }.trimEnd('=')
    val output = ArrayList<Byte>(clean.length * 3 / 4)
    var buffer = 0
    var bits = 0
    clean.forEach { char ->
        val index = alphabet.indexOf(char)
        require(index >= 0)
        buffer = (buffer shl 6) or index
        bits += 6
        if (bits >= 8) {
            bits -= 8
            output += ((buffer shr bits) and 0xff).toByte()
        }
    }
    return output.toByteArray()
}

expect fun imageBitmapFromBytes(bytes: ByteArray): ImageBitmap?
