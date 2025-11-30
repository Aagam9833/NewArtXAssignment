package com.aagamshah.newartxassignment.data.repositoryimpl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aagamshah.newartxassignment.data.model.toDomain
import com.aagamshah.newartxassignment.data.remote.ApiService
import com.aagamshah.newartxassignment.domain.model.User

class UserPagingSource(private val api: ApiService) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val page = params.key ?: 1
            val limit = params.loadSize
            val skip = (page - 1) * limit

            val response = api.getUsers(limit, skip)
            val users = response.users.map { it.toDomain() }

            LoadResult.Page(
                data = users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (users.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}