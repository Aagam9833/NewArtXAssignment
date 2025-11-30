package com.aagamshah.newartxassignment.data.repositoryimpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.aagamshah.newartxassignment.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    UserPreferencesRepository {

    private val KEY_OFFLINE_MODE = booleanPreferencesKey("offline_only_mode")
    private val KEY_AUTO_REFRESH = booleanPreferencesKey("auto_refresh_enabled")
    private val KEY_LAST_REFRESH = longPreferencesKey("last_refresh_timestamp")

    override val offlineModeFlow: Flow<Boolean> = dataStore.data
        .map { it[KEY_OFFLINE_MODE] ?: false }

    override val autoRefreshFlow: Flow<Boolean> = dataStore.data
        .map { it[KEY_AUTO_REFRESH] ?: true } // Default true

    override val lastRefreshFlow: Flow<Long> = dataStore.data
        .map { it[KEY_LAST_REFRESH] ?: 0L }

    override suspend fun setOfflineMode(enabled: Boolean) {
        dataStore.edit { it[KEY_OFFLINE_MODE] = enabled }
    }

    override suspend fun setAutoRefresh(enabled: Boolean) {
        dataStore.edit { it[KEY_AUTO_REFRESH] = enabled }
    }

    override suspend fun updateLastRefreshTime(time: Long) {
        dataStore.edit { it[KEY_LAST_REFRESH] = time }
    }
}