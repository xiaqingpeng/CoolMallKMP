package com.joker.coolmall.feature.common

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.ui.component.TitleWithLine
import kotlinx.coroutines.delay
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.about_api_doc
import myapplication.shared.generated.resources.about_api_doc_desc
import myapplication.shared.generated.resources.about_app_name
import myapplication.shared.generated.resources.about_citation
import myapplication.shared.generated.resources.about_citation_desc
import myapplication.shared.generated.resources.about_copyright
import myapplication.shared.generated.resources.about_demo_download
import myapplication.shared.generated.resources.about_demo_download_desc
import myapplication.shared.generated.resources.about_developer
import myapplication.shared.generated.resources.about_developer_email
import myapplication.shared.generated.resources.about_developer_name
import myapplication.shared.generated.resources.about_gitee
import myapplication.shared.generated.resources.about_gitee_url
import myapplication.shared.generated.resources.about_github
import myapplication.shared.generated.resources.about_github_discussion
import myapplication.shared.generated.resources.about_github_discussion_desc
import myapplication.shared.generated.resources.about_github_url
import myapplication.shared.generated.resources.about_help
import myapplication.shared.generated.resources.about_help_desc
import myapplication.shared.generated.resources.about_icon_source
import myapplication.shared.generated.resources.about_icon_source_desc
import myapplication.shared.generated.resources.about_open_source_license
import myapplication.shared.generated.resources.about_privacy_policy
import myapplication.shared.generated.resources.about_qq_group
import myapplication.shared.generated.resources.about_section_discussion
import myapplication.shared.generated.resources.about_section_other
import myapplication.shared.generated.resources.about_section_project
import myapplication.shared.generated.resources.about_section_resources
import myapplication.shared.generated.resources.about_section_support
import myapplication.shared.generated.resources.about_support
import myapplication.shared.generated.resources.about_support_desc
import myapplication.shared.generated.resources.about_translation
import myapplication.shared.generated.resources.about_translation_desc
import myapplication.shared.generated.resources.about_user_agreement
import myapplication.shared.generated.resources.about_version
import myapplication.shared.generated.resources.about_version_code
import myapplication.shared.generated.resources.about_wechat_group
import myapplication.shared.generated.resources.app_name
import myapplication.shared.generated.resources.avatar
import myapplication.shared.generated.resources.ic_logo
import myapplication.shared.generated.resources.me_about
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutRoute(
    onBack: () -> Unit,
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onContributorsClick: () -> Unit = {},
    onWebClick: (String, String) -> Unit = { _, _ -> },
) {
    val scrollState = rememberScrollState()
    var ready by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        ready = true
    }
    val scrollFraction by remember {
        derivedStateOf { (scrollState.value / 220f).coerceIn(0f, 1f) }
    }
    val heroAlpha by animateFloatAsState(
        targetValue = if (ready) 1f - scrollFraction else 0f,
        animationSpec = tween(800),
        label = "about_hero_alpha",
    )
    val heroOffset by animateDpAsState(
        targetValue = if (ready) 0.dp else 30.dp,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "about_hero_offset",
    )

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = .18f),
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                ),
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(360.dp))
            AboutInfoCard()
            Spacer(Modifier.height(24.dp))
            AboutSection(stringResource(Res.string.about_developer)) {
                AboutDeveloperLink(
                    onClick = {
                        onWebClick("https://github.com/Joker-x-dev", "Joker.X")
                    },
                )
                AboutLink("贡献者", onClick = onContributorsClick, showDivider = false)
            }
            AboutSection(stringResource(Res.string.about_section_project)) {
                AboutLink(stringResource(Res.string.about_github), stringResource(Res.string.about_github_url)) {
                    onWebClick("https://github.com/Joker-x-dev/CoolMallKotlin", "GitHub")
                }
                AboutLink(stringResource(Res.string.about_gitee), stringResource(Res.string.about_gitee_url), showDivider = false) {
                    onWebClick("https://gitee.com/Joker-x-dev/CoolMallKotlin", "Gitee")
                }
            }
            AboutSection(stringResource(Res.string.about_section_resources)) {
                AboutLink(stringResource(Res.string.about_api_doc), stringResource(Res.string.about_api_doc_desc)) {
                    onWebClick("https://coolmall.apifox.cn", "API 文档")
                }
                AboutLink(stringResource(Res.string.about_demo_download), stringResource(Res.string.about_demo_download_desc)) {
                    onWebClick("https://www.pgyer.com/CoolMallKotlinProdRelease", "Demo 下载")
                }
                AboutLink(stringResource(Res.string.about_icon_source), stringResource(Res.string.about_icon_source_desc), showDivider = false) {
                    onWebClick("https://github.com/tuniaoTech", "图标来源")
                }
            }
            AboutSection(stringResource(Res.string.about_section_discussion)) {
                AboutLink(stringResource(Res.string.about_github_discussion), stringResource(Res.string.about_github_discussion_desc)) {
                    onWebClick("https://github.com/Joker-x-dev/CoolMallKotlin/discussions", "GitHub 讨论区")
                }
                AboutLink(stringResource(Res.string.about_qq_group))
                AboutLink(stringResource(Res.string.about_wechat_group), showDivider = false)
            }
            AboutSection(stringResource(Res.string.about_section_support)) {
                AboutLink(stringResource(Res.string.about_translation), stringResource(Res.string.about_translation_desc)) {
                    onWebClick("https://github.com/Joker-x-dev/CoolMallKotlin/issues", "翻译")
                }
                AboutLink(stringResource(Res.string.about_support), stringResource(Res.string.about_support_desc)) {
                    onWebClick("https://github.com/Joker-x-dev/CoolMallKotlin", "支持")
                }
                AboutLink(stringResource(Res.string.about_help), stringResource(Res.string.about_help_desc), showDivider = false) {
                    onWebClick("https://github.com/Joker-x-dev/CoolMallKotlin/issues", "帮助")
                }
            }
            AboutSection(stringResource(Res.string.about_section_other)) {
                AboutLink(stringResource(Res.string.about_citation), stringResource(Res.string.about_citation_desc), onClick = onContributorsClick)
                AboutLink(stringResource(Res.string.about_user_agreement), onClick = onUserAgreementClick)
                AboutLink(stringResource(Res.string.about_privacy_policy), onClick = onPrivacyPolicyClick)
                AboutLink(stringResource(Res.string.about_open_source_license), showDivider = false) {
                    onWebClick("https://github.com/Joker-x-dev/CoolMallKotlin/blob/main/LICENSE", "开源许可")
                }
            }
            Text(
                text = stringResource(Res.string.about_copyright),
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = heroAlpha
                    translationY = heroOffset.toPx()
                    scaleX = 1f - scrollFraction * .08f
                    scaleY = 1f - scrollFraction * .08f
                },
        ) {
            AboutHero(Modifier.fillMaxWidth())
        }
        CenterAlignedTopAppBar(
            title = { Text(stringResource(Res.string.me_about), Modifier.graphicsLayer { alpha = scrollFraction }) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = scrollFraction),
            ),
        )
    }
}

@Composable
private fun AboutHero(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(top = 130.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .5f),
            ),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(Res.drawable.ic_logo),
                    contentDescription = stringResource(Res.string.app_name),
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(24.dp)),
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(stringResource(Res.string.app_name), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Version ${AppContainerProvider.versionName.ifBlank { "—" }}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AboutInfoCard() {
    Card(Modifier.fillMaxWidth()) {
        Column {
            AboutInfoRow(stringResource(Res.string.about_app_name), stringResource(Res.string.app_name), showDivider = true)
            AboutInfoRow(stringResource(Res.string.about_version), AppContainerProvider.versionName.ifBlank { "—" }, showDivider = true)
            AboutInfoRow(stringResource(Res.string.about_version_code), AppContainerProvider.versionCode.ifBlank { "—" }, showDivider = false)
        }
    }
}

@Composable
private fun AboutSection(title: String, content: @Composable () -> Unit) {
    Column {
        TitleWithLine(text = title)
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) { Column { content() } }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AboutLink(
    title: String,
    description: String? = null,
    showDivider: Boolean = true,
    onClick: () -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                if (!description.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Composable
private fun AboutDeveloperLink(onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.avatar),
                contentDescription = stringResource(Res.string.about_developer_name),
                modifier = Modifier
                    .size(36.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(stringResource(Res.string.about_developer_name), style = MaterialTheme.typography.titleLarge)
                Text(
                    stringResource(Res.string.about_developer_email),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
private fun AboutInfoRow(label: String, value: String, showDivider: Boolean) {
    Column {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, textAlign = TextAlign.End)
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

