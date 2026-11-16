plugins {
    alias(libs.plugins.coolmallkmp.android.library)
    alias(libs.plugins.coolmallkmp.hilt)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    // Navigation3 Runtime
    implementation(libs.androidx.navigation3.runtime)

    // Kotlin 序列化
    implementation(libs.kotlinx.serialization.json)

    // 全局应用状态（登录态拦截）
    implementation(project(":core:data"))

    // 数据模型（用于 NavigationResultKey 绑定具体实体，例如 Address）
    implementation(project(":core:model"))
}

android {
    namespace = "com.joker.coolmallkmp.core.navigation"
}
