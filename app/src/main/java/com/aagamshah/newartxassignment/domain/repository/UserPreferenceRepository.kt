package com.aagamshah.newartxassignment.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val offlineModeFlow: Flow<Boolean>
    val autoRefreshFlow: Flow<Boolean>
    val lastRefreshFlow: Flow<Long>

    suspend fun setOfflineMode(enabled: Boolean)
    suspend fun setAutoRefresh(enabled: Boolean)
    suspend fun updateLastRefreshTime(time: Long)
}