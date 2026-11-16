package com.joker.coolmallkmp.core.platform

import android.content.Intent
import android.net.Uri
import com.joker.coolmallkmp.core.di.AppContainerProvider

actual fun openExternalUrl(url: String) {
    val context = AppContainerProvider.appContext as? android.content.Context ?: return
    context.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}
