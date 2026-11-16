package com.joker.coolmallkmp.core.platform

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * 跟踪前台 Activity，供支付宝 PayTask 使用。
 */
object CurrentActivityHolder {
    @Volatile
    private var ref: WeakReference<Activity>? = null

    val current: Activity?
        get() = ref?.get()

    fun update(activity: Activity?) {
        ref = if (activity == null) null else WeakReference(activity)
    }

    fun install(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) {
                update(activity)
            }
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) {
                if (ref?.get() === activity) update(null)
            }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) {
                if (ref?.get() === activity) update(null)
            }
        })
    }
}
