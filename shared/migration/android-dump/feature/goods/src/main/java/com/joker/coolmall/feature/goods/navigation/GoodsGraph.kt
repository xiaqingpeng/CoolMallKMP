package com.joker.coolmallkmp.feature.goods.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.joker.coolmallkmp.navigation.goods.GoodsRoutes
import com.joker.coolmallkmp.feature.goods.view.GoodsCategoryRoute
import com.joker.coolmallkmp.feature.goods.view.GoodsCommentRoute
import com.joker.coolmallkmp.feature.goods.view.GoodsDetailRoute
import com.joker.coolmallkmp.feature.goods.view.GoodsSearchRoute

/**
 * 商品模块导航图
 *
 * @author Joker.X
 */
fun EntryProviderScope<NavKey>.goodsGraph() {
    entry<GoodsRoutes.Detail> { key ->
        GoodsDetailRoute(navKey = key)
    }
    entry<GoodsRoutes.Search> {
        GoodsSearchRoute()
    }
    entry<GoodsRoutes.Comment> { key ->
        GoodsCommentRoute(navKey = key)
    }
    entry<GoodsRoutes.Category> { key ->
        GoodsCategoryRoute(navKey = key)
    }
}
