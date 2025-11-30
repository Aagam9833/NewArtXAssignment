package com.aagamshah.newartxassignment.navigation

import kotlinx.serialization.Serializable

@Serializable
object MainRoute

@Serializable
sealed interface Routes {
    @Serializable
    object HomeScreen : Routes

    @Serializable
    data class ProfileRoute(val id: Int)

    @Serializable
    object SettingsRoute

}

