package com.joker.coolmallkmp.core.data.local

import com.russhwolf.settings.Settings

/**
 * Shared persistent storage used by appsettings-kmp for the app language.
 * It is deliberately separate from the DataStore-backed business preferences:
 * appsettings owns its key namespace and platform locale integration.
 */
val appLanguageSettings: Settings by lazy { Settings() }

val supportedAppLanguages: List<String> = listOf("zh", "en")
