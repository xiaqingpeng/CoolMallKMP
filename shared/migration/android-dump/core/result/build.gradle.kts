plugins {
    alias(libs.plugins.coolmallkmp.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.joker.coolmallkmp.result"
}
dependencies {
    // 引入 model 模块
    implementation(projects.core.model)
    // 引入 util 模块
    implementation(projects.core.util)
    // kotlin序列化
    implementation(libs.kotlinx.serialization.json)
}