package com.aagamshah.newartxassignment.presentation.settingsscreen

import com.aagamshah.newartxassignment.utils.ConnectivityStatus

data class SettingsUiState(
    val isOfflineMode: Boolean = false,
    val isAutoRefresh: Boolean = true,
    val connectivityStatus: ConnectivityStatus = ConnectivityStatus.Unavailable,
    val lastRefreshTime: String = "Never"
)