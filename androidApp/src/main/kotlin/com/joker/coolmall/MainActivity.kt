package com.joker.coolmall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.joker.coolmall.core.platform.onVerificationNotificationPermissionResult
import com.joker.coolmall.ui.CoolMallApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initializeCoolMallAndroid(
            context = this,
            baseUrl = "https://mall.dusksnow.top/app/",
            isDebug = true,
        )

        setContent {
            CoolMallApp()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2001) {
            onVerificationNotificationPermissionResult(
                grantResults.firstOrNull() == android.content.pm.PackageManager.PERMISSION_GRANTED,
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    CoolMallApp()
}
