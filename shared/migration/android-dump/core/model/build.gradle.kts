plugins {
    alias(libs.plugins.coolmallkmp.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.joker.coolmallkmp.core.model"
}

kotlin {
    sourceSets.all {
        languageSettings {
            optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}

dependencies {
    // kotlin序列化
    implementation(libs.kotlinx.serialization.json)
}