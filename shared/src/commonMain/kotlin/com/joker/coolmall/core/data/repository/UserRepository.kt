package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.data.local.UserPreferences
import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorUserNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class UserRepository(
    private val dataSource: KtorUserNetworkDataSource,
    private val userPreferences: UserPreferences,
) {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    suspend fun restoreFromCache() {
        _user.value = userPreferences.getUser()
    }

    suspend fun refreshPersonInfo(): NetworkResponse<User> = withContext(Dispatchers.IO) {
        dataSource.getPersonInfo().also { response ->
            if (response.isSucceeded && response.data != null) {
                val user = response.data
                userPreferences.saveUser(user)
                _user.value = user
            }
        }
    }

    suspend fun updatePassword(
        oldPassword: String,
        newPassword: String,
    ): NetworkResponse<Unit> = withContext(Dispatchers.IO) {
        dataSource.updatePassword(
            mapOf(
                "oldPassword" to oldPassword,
                "newPassword" to newPassword,
            ),
        )
    }

    suspend fun logoff(): NetworkResponse<Unit> = withContext(Dispatchers.IO) {
        dataSource.logoff()
    }

    suspend fun clear() {
        userPreferences.clearUser()
        _user.value = null
    }
}
