package com.joker.coolmall.feature.goods.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.joker.coolmall.feature.goods.GoodsCategoryRoute
import kotlinx.serialization.Serializable

@Serializable
data class GoodsCategoryRouteArgs(
    val typeId: Long = -1L,
    val title: String = "",
    val featured: Boolean = false,
    val recommend: Boolean = false,
    val keyword: String = "",
)

fun NavController.navigateToGoodsCategory(
    typeId: Long? = null,
    title: String = "",
    featured: Boolean = false,
    recommend: Boolean = false,
    keyword: String? = null,
) {
    navigate(
        GoodsCategoryRouteArgs(
            typeId = typeId ?: -1L,
            title = title,
            featured = featured,
            recommend = recommend,
            keyword = keyword.orEmpty(),
        )
    )
}

fun NavGraphBuilder.goodsCategoryScreen(
    onBack: () -> Unit,
    onGoodsClick: (Long) -> Unit,
) {
    composable<GoodsCategoryRouteArgs> { entry ->
        val args = entry.toRoute<GoodsCategoryRouteArgs>()
        GoodsCategoryRoute(
            typeId = args.typeId.takeIf { it > 0 },
            title = args.title,
            featured = args.featured,
            recommend = args.recommend,
            keyword = args.keyword.takeIf { it.isNotBlank() },
            onBack = onBack,
            onGoodsClick = onGoodsClick,
        )
    }
}
