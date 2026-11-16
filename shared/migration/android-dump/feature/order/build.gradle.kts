plugins {
    alias(libs.plugins.coolmallkmp.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.joker.coolmallkmp.feature.order"
}

dependencies {
    //支付宝支付
    implementation(libs.alipaysdk.android)
    // kotlin序列化
    implementation(libs.kotlinx.serialization.json)
}