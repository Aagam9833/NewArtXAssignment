package com.aagamshah.newartxassignment.presentation.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aagamshah.newartxassignment.data.infrastructure.NetworkConnectivityObserver
import com.aagamshah.newartxassignment.domain.repository.UserPreferencesRepository
import com.aagamshah.newartxassignment.utils.ConnectivityStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewModel(
    private val preferences: UserPreferencesRepository,
    connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferences.offlineModeFlow,
        preferences.autoRefreshFlow,
        preferences.lastRefreshFlow,
        connectivityObserver.observe().onStart { emit(ConnectivityStatus.Unavailable) }
    ) { offline, autoRefresh, lastTime, status ->
        SettingsUiState(
            isOfflineMode = offline,
            isAutoRefresh = autoRefresh,
            connectivityStatus = status,
            lastRefreshTime = if (lastTime == 0L) "Never" else formatTime(lastTime)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    init {
        viewModelScope.launch {
            tickerFlow(2 * 60 * 1000)
                .combine(uiState) { _, state -> state }
                .collect { state ->
                    if (state.isAutoRefresh &&
                        !state.isOfflineMode &&
                        state.connectivityStatus == ConnectivityStatus.Available
                    ) {
                        performBackgroundSync()
                    }
                }
        }
    }

    private suspend fun performBackgroundSync() {
        preferences.updateLastRefreshTime(System.currentTimeMillis())
    }

    fun toggleOfflineMode(enabled: Boolean) {
        viewModelScope.launch { preferences.setOfflineMode(enabled) }
    }

    fun toggleAutoRefresh(enabled: Boolean) {
        viewModelScope.launch { preferences.setAutoRefresh(enabled) }
    }

    private fun tickerFlow(period: Long) = flow {
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private fun formatTime(millis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    class Factory(
        private val prefs: UserPreferencesRepository,
        private val connectivity: NetworkConnectivityObserver
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(prefs, connectivity) as T
        }
    }
}