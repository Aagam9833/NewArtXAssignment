package com.aagamshah.newartxassignment.domain.repository

import androidx.paging.PagingData
import com.aagamshah.newartxassignment.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(query: String): Flow<PagingData<User>>
}