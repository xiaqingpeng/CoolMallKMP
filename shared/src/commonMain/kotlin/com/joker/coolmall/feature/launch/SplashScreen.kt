package com.joker.coolmall.feature.launch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.splash_brand
import myapplication.shared.generated.resources.splash_subtitle
import myapplication.shared.generated.resources.ic_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashRoute(
    toGuide: () -> Unit,
    toMain: () -> Unit,
    viewModel: SplashViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SplashViewModel(
                    guidePreferences = AppContainerProvider.instance.guidePreferences,
                    authRepository = AppContainerProvider.instance.authRepository,
                    splashPreferences = AppContainerProvider.instance.splashPreferences,
                )
            }
        }
    ),
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Guide -> toGuide()
            SplashDestination.Main -> toMain()
            null -> Unit
        }
    }

    SplashScreen()
}

@Composable
private fun SplashScreen() {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        started = true
    }

    val offsetY by animateDpAsState(
        targetValue = if (started) 0.dp else 160.dp,
        animationSpec = tween(1000),
        label = "splash_logo_offset",
    )
    val rotation by animateFloatAsState(
        targetValue = if (started) 0f else -15f,
        animationSpec = tween(1000),
        label = "splash_logo_rotation",
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(800),
        label = "splash_logo_alpha",
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(800, delayMillis = 400),
        label = "splash_text_alpha",
    )

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.size(240.dp).blur(100.dp).alpha(0.8f),
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .offset(y = offsetY)
                    .rotate(rotation)
                    .alpha(logoAlpha),
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(textAlpha)) {
                Text(
                    text = stringResource(Res.string.splash_brand),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(Res.string.splash_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}
