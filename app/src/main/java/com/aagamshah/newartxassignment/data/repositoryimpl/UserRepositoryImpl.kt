package com.aagamshah.newartxassignment.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aagamshah.newartxassignment.data.AppDatabase
import com.aagamshah.newartxassignment.data.remote.ApiService
import com.aagamshah.newartxassignment.data.repositoryimpl.UserRemoteMediator
import com.aagamshah.newartxassignment.domain.model.User
import com.aagamshah.newartxassignment.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class UserRepositoryImpl(
    private val api: ApiService,
    private val database: AppDatabase
) : UserRepository {

    override fun getUsers(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            remoteMediator = UserRemoteMediator(query, api, database),

            pagingSourceFactory = { database.userDao().pagingSource(query) }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                User(
                    id = entity.id,
                    firstName = entity.firstName,
                    lastName = entity.lastName,
                    email = entity.email,
                    image = entity.image
                )
            }
        }
    }
}