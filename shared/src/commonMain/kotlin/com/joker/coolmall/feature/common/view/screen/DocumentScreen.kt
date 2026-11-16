package com.joker.coolmallkmp.feature.common.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.feature.main.component.CenterTopAppBar
import com.joker.coolmallkmp.feature.common.viewmodel.DocumentUiState
import com.joker.coolmallkmp.feature.common.viewmodel.DocumentViewModel
import com.joker.coolmallkmp.feature.common.view.platform.PlatformHtmlDocument
import com.joker.coolmallkmp.feature.common.view.skeleton.DocumentLoadingSkeleton

@Composable
fun DocumentRoute(
    title: String,
    paramKey: String,
    onBack: () -> Unit,
) {
    val container = AppContainerProvider.instance
    val vm: DocumentViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                DocumentViewModel(
                    paramKey = paramKey,
                    commonRepository = container.commonRepository,
                )
            }
        },
    )
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = title,
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when (val current = state) {
                DocumentUiState.Loading -> DocumentLoadingSkeleton()
                is DocumentUiState.Error -> Text(current.message.toString())
                is DocumentUiState.Success -> PlatformHtmlDocument(
                    html = current.html,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
