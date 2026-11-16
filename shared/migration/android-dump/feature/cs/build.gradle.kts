plugins {
    alias(libs.plugins.coolmallkmp.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.joker.coolmallkmp.feature.cs"
}
dependencies {
    // 网络请求
    implementation(libs.okhttp3)
    // kotlin序列化
    implementation(libs.kotlinx.serialization.json)
}