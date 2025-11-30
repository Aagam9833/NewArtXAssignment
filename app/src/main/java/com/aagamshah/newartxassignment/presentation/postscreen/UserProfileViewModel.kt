package com.aagamshah.newartxassignment.presentation.postscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aagamshah.newartxassignment.data.infrastructure.NetworkConnectivityObserver
import com.aagamshah.newartxassignment.domain.model.Post
import com.aagamshah.newartxassignment.domain.model.User
import com.aagamshah.newartxassignment.domain.repository.ProfileRepository
import com.aagamshah.newartxassignment.domain.repository.UserPreferencesRepository
import com.aagamshah.newartxassignment.utils.ConnectivityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val isLoadingPosts: Boolean = false
)

class UserProfileViewModel(
    private val userId: Int,
    private val repository: ProfileRepository,
    private val preferences: UserPreferencesRepository,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _user = MutableStateFlow<User?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(
        _user,
        repository.getPostsFlow(userId),
        _isLoading
    ) { user, posts, loading ->
        ProfileUiState(user, posts, loading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoadingPosts = true)
    )

    init {
        loadUserDetails()

        monitorNetworkAndRefresh()
    }

    private fun loadUserDetails() {
        viewModelScope.launch {
            _user.value = repository.getUserDetails(userId)
        }
    }

    private fun monitorNetworkAndRefresh() {
        viewModelScope.launch {
            combine(
                preferences.offlineModeFlow,
                connectivityObserver.observe()
            ) { isOfflineMode, networkStatus ->
                !isOfflineMode && networkStatus == ConnectivityStatus.Available
            }
                .distinctUntilChanged()
                .filter { canFetch -> canFetch }
                .collect {
                    refreshData()
                }
        }
    }

    private suspend fun refreshData() {
        _isLoading.value = true
        repository.refreshPosts(userId)
        _isLoading.value = false
    }

    class Factory(
        private val userId: Int,
        private val repository: ProfileRepository,
        private val preferences: UserPreferencesRepository,
        private val connectivityObserver: NetworkConnectivityObserver
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return UserProfileViewModel(userId, repository, preferences, connectivityObserver) as T
        }
    }
}