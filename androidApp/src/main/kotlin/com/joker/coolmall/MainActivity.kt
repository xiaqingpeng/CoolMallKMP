package com.joker.coolmall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.joker.coolmall.ui.CoolMallApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
}

@Preview
@Composable
fun AppAndroidPreview() {
    CoolMallApp()
}
