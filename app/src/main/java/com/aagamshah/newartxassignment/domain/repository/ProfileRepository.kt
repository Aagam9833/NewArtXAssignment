package com.aagamshah.newartxassignment.domain.repository

import com.aagamshah.newartxassignment.domain.model.Post
import com.aagamshah.newartxassignment.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getUserDetails(userId: Int): User?
    fun getPostsFlow(userId: Int): Flow<List<Post>>
    suspend fun refreshPosts(userId: Int)
}