package com.joker.coolmall.feature.launch

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.ui.component.AppButton
import com.joker.coolmall.core.ui.component.AppButtonSize
import com.joker.coolmall.core.ui.component.AppButtonStyle
import kotlinx.coroutines.launch
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.guide_next
import myapplication.shared.generated.resources.guide_page1_desc
import myapplication.shared.generated.resources.guide_page1_title
import myapplication.shared.generated.resources.guide_page2_desc
import myapplication.shared.generated.resources.guide_page2_title
import myapplication.shared.generated.resources.guide_page3_desc
import myapplication.shared.generated.resources.guide_page3_title
import myapplication.shared.generated.resources.guide_page4_desc
import myapplication.shared.generated.resources.guide_page4_title
import myapplication.shared.generated.resources.guide_skip
import myapplication.shared.generated.resources.guide_start
import myapplication.shared.generated.resources.ic_add_to_cart
import myapplication.shared.generated.resources.ic_book_writer
import myapplication.shared.generated.resources.ic_dev_productivity
import myapplication.shared.generated.resources.ic_home_screen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Suppress("ParamsComparedByRef")
@Composable
fun GuideRoute(
    toMain: () -> Unit,
    viewModel: GuideViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                GuideViewModel(AppContainerProvider.instance.guidePreferences)
            }
        }
    ),
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { viewModel.pageCount })

    LaunchedEffect(Unit) {
        viewModel.navigateToMain.collect { toMain() }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.updatePageIndex(page)
        }
    }

    GuideScreen(
        currentPage = currentPage,
        pageCount = viewModel.pageCount,
        isLastPage = viewModel.isLastPage(),
        pagerState = pagerState,
        onSkip = viewModel::skipGuide,
        onNext = {
            if (viewModel.handleNextClick()) {
                scope.launch {
                    pagerState.animateScrollToPage(viewModel.nextPageIndex())
                }
            }
        },
    )
}

@Composable
private fun GuideScreen(
    currentPage: Int,
    pageCount: Int,
    isLastPage: Boolean,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onSkip: () -> Unit,
    onNext: () -> Unit,
) {
    val titles = listOf(
        stringResource(Res.string.guide_page1_title),
        stringResource(Res.string.guide_page2_title),
        stringResource(Res.string.guide_page3_title),
        stringResource(Res.string.guide_page4_title),
    )
    val descs = listOf(
        stringResource(Res.string.guide_page1_desc),
        stringResource(Res.string.guide_page2_desc),
        stringResource(Res.string.guide_page3_desc),
        stringResource(Res.string.guide_page4_desc),
    )
    val illustrations = listOf(
        Res.drawable.ic_book_writer,
        Res.drawable.ic_home_screen,
        Res.drawable.ic_add_to_cart,
        Res.drawable.ic_dev_productivity,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopEnd)
                .padding(8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onSkip) {
                Text(
                    text = stringResource(Res.string.guide_skip),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(
                            illustrations.getOrElse(page) { Res.drawable.ic_book_writer },
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(240.dp),
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = titles.getOrElse(page) { "" },
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = descs.getOrElse(page) { "" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(pageCount) { index ->
                    val selected = index == currentPage
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                        .width(if (selected) 24.dp else 8.dp)
                            .background(
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = CircleShape,
                            ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val buttonWidth by animateDpAsState(
                targetValue = if (isLastPage) 160.dp else 120.dp,
                animationSpec = tween(300),
                label = "guide_btn_width",
            )
            AppButton(
                text = if (isLastPage) {
                    stringResource(Res.string.guide_start)
                } else {
                    stringResource(Res.string.guide_next)
                },
                onClick = onNext,
                size = AppButtonSize.MEDIUM,
                style = AppButtonStyle.GRADIENT,
                fullWidth = false,
                modifier = Modifier.width(buttonWidth),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
