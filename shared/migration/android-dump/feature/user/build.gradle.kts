plugins {
    alias(libs.plugins.coolmallkmp.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.joker.coolmallkmp.feature.user"
}
dependencies {
    // kotlin序列化
    implementation(libs.kotlinx.serialization.json)
}