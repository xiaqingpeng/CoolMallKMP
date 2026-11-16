plugins {
    alias(libs.plugins.coolmallkmp.android.feature)
}

android {
    namespace = "com.joker.coolmallkmp.feature.main"
}
dependencies {
    // lottie 动画
    // https://airbnb.io/lottie/#/android-compose
    implementation(libs.lottie.compose)
}