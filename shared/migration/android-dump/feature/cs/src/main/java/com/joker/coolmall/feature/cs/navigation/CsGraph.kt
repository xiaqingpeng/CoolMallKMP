package com.joker.coolmallkmp.feature.cs.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.joker.coolmallkmp.navigation.cs.CsRoutes
import com.joker.coolmallkmp.feature.cs.view.ChatRoute

/**
 * 客服模块导航图
 *
 * @author Joker.X
 */
fun EntryProviderScope<NavKey>.csGraph() {
    entry<CsRoutes.Chat> {
        ChatRoute()
    }
}
