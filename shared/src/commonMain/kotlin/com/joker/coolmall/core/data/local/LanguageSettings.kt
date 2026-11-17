package com.joker.coolmallkmp.core.data.local

import com.russhwolf.settings.Settings
import wang.harlon.appsettings.language.APP_LANGUAGE_STORAGE_KEY

enum class AppLanguage(val tag: String?) {
    SYSTEM(null),
    CHINESE("zh"),
    ENGLISH("en"),
}

/**
 * Shared persistent storage used by appsettings-kmp for the app language.
 * It is deliberately separate from the DataStore-backed business preferences:
 * appsettings owns its key namespace and platform locale integration.
 */
val appLanguageSettings: Settings by lazy { Settings() }

val supportedAppLanguages: List<String> = listOf("zh", "en")

expect fun applyAppLanguage(tag: String?)

fun currentAppLanguage(): AppLanguage = when (
    appLanguageSettings.getString(APP_LANGUAGE_STORAGE_KEY, "system")
) {
    "zh" -> AppLanguage.CHINESE
    "en" -> AppLanguage.ENGLISH
    else -> AppLanguage.SYSTEM
}

fun selectAppLanguage(language: AppLanguage) {
    appLanguageSettings.putString(
        APP_LANGUAGE_STORAGE_KEY,
        language.tag ?: "system",
    )
    applyAppLanguage(language.tag)
}
