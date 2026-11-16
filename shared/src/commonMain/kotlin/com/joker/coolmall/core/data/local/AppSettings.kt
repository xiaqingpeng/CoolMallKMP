package com.joker.coolmallkmp.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.locks.SynchronizedObject
import io.ktor.utils.io.locks.synchronized
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "coolmall_settings.preferences_pb"

object AppSettings {
    private lateinit var dataStore: DataStore<Preferences>

    @OptIn(InternalAPI::class)
    private val lock = SynchronizedObject()

    @OptIn(InternalAPI::class)
    fun getDataStore(producePath: () -> String): DataStore<Preferences> {
        return synchronized(lock) {
            if (::dataStore.isInitialized) {
                dataStore
            } else {
                PreferenceDataStoreFactory.createWithPath(
                    produceFile = { producePath().toPath() }
                ).also { dataStore = it }
            }
        }
    }
}
