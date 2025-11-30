package com.aagamshah.newartxassignment.data.remote

import android.content.Context
import androidx.room.Room
import com.aagamshah.newartxassignment.data.AppDatabase
import com.aagamshah.newartxassignment.data.infrastructure.NetworkConnectivityObserver
import com.aagamshah.newartxassignment.data.local.dataStore
import com.aagamshah.newartxassignment.data.repositoryimpl.ProfileRepositoryImpl
import com.aagamshah.newartxassignment.data.repositoryimpl.UserPreferencesRepositoryImpl
import com.aagamshah.newartxassignment.data.repositoryimpl.UserRepositoryImpl
import com.aagamshah.newartxassignment.domain.repository.ProfileRepository
import com.aagamshah.newartxassignment.domain.repository.UserPreferencesRepository
import com.aagamshah.newartxassignment.domain.repository.UserRepository
import com.aagamshah.newartxassignment.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(private val context: Context) {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApi: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "user_database.db"
        ).build()
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(context.dataStore)
    }

    val connectivityObserver by lazy {
        NetworkConnectivityObserver(context)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userApi, database, userPreferencesRepository)
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(userApi, database, userPreferencesRepository)
    }
}