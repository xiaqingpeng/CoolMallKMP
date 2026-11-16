plugins {
    alias(libs.plugins.coolmallkmp.android.library)
    alias(libs.plugins.coolmallkmp.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.joker.coolmallkmp.core.datastore"
}

dependencies {
    // 引入 util 模块
    implementation(projects.core.util)
    // 引入 model 模块
    implementation(projects.core.model)
    // kotlin序列化
    implementation(libs.kotlinx.serialization.json)
}