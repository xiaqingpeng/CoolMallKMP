package com.joker.coolmall.feature.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.ui.empty.EmptyUiMessage
import com.joker.coolmall.feature.main.component.CenterTopAppBar
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_back
import myapplication.shared.generated.resources.common_retry
import org.jetbrains.compose.resources.stringResource

/**
 * 用户协议 / 隐私政策：拉取 base/comm/param，Android/iOS 分别使用原生 HTML 容器展示。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentRoute(
    title: String,
    paramKey: String,
    onBack: () -> Unit,
    viewModel: DocumentViewModel = viewModel(
        key = "document_$paramKey",
        factory = viewModelFactory {
            initializer {
                DocumentViewModel(
                    paramKey = paramKey,
                    commonRepository = AppContainerProvider.instance.commonRepository,
                )
            }
        }
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = title,
                showBackIcon = true,
                onBackClick = onBack,
            )
        },
    ) { padding ->
        when (val state = uiState) {
            DocumentUiState.Loading -> {
                DocumentLoadingSkeleton(Modifier.padding(padding))
            }

            is DocumentUiState.Error -> {
                EmptyUiMessage(
                    message = state.message,
                    onRetryClick = viewModel::load,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }

            is DocumentUiState.Success -> {
                PlatformHtmlDocument(
                    html = state.html,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
        }
    }
}
