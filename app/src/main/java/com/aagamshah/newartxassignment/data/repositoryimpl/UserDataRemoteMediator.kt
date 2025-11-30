package com.aagamshah.newartxassignment.data.repositoryimpl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aagamshah.newartxassignment.data.AppDatabase
import com.aagamshah.newartxassignment.data.model.RemoteKeys
import com.aagamshah.newartxassignment.data.model.UserEntity
import com.aagamshah.newartxassignment.data.remote.ApiService

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val query: String,
    private val api: ApiService,
    private val database: AppDatabase
) : RemoteMediator<Int, UserEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        return try {
            val skip = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    val remoteKeys = database.userDao().remoteKeysUserId(lastItem.id)
                    remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val limit = state.config.pageSize
            val response = if (query.isEmpty()) {
                api.getUsers(limit, skip)
            } else {
                api.searchUsers(query, limit, skip)
            }

            val users = response.users
            val endOfPaginationReached = users.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.userDao().clearRemoteKeys()
                    database.userDao().clearUsers()
                }

                val prevKey = if (skip == 0) null else skip - limit
                val nextKey = if (endOfPaginationReached) null else skip + limit

                val keys = users.map {
                    RemoteKeys(userId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                val entities = users.map {
                    UserEntity(it.id, it.firstName, it.lastName, it.email, it.image)
                }

                database.userDao().insertRemoteKeys(keys)
                database.userDao().insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}