package com.joker.coolmall.feature.launch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.splash_brand
import myapplication.shared.generated.resources.splash_skip
import myapplication.shared.generated.resources.splash_subtitle
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

    SplashScreen(onSkip = viewModel::skipToMain)
}

@Composable
private fun SplashScreen(
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.splash_brand),
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            text = stringResource(Res.string.splash_subtitle),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onSkip) {
            Text(stringResource(Res.string.splash_skip))
        }
    }
}
