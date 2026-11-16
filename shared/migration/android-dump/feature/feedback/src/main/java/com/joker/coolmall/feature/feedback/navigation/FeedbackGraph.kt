package com.joker.coolmallkmp.feature.feedback.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.joker.coolmallkmp.navigation.feedback.FeedbackRoutes
import com.joker.coolmallkmp.feature.feedback.view.FeedbackListRoute
import com.joker.coolmallkmp.feature.feedback.view.FeedbackSubmitRoute

/**
 * 反馈模块导航图
 *
 * @author Joker.X
 */
fun EntryProviderScope<NavKey>.feedbackGraph() {
    entry<FeedbackRoutes.List> {
        FeedbackListRoute()
    }
    entry<FeedbackRoutes.Submit> {
        FeedbackSubmitRoute()
    }
}
