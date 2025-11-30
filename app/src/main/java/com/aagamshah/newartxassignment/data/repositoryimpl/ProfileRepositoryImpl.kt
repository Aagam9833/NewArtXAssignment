package com.aagamshah.newartxassignment.data.repositoryimpl

import com.aagamshah.newartxassignment.data.AppDatabase
import com.aagamshah.newartxassignment.data.entity.PostEntity
import com.aagamshah.newartxassignment.data.remote.ApiService
import com.aagamshah.newartxassignment.domain.model.Post
import com.aagamshah.newartxassignment.domain.model.User
import com.aagamshah.newartxassignment.domain.repository.ProfileRepository
import com.aagamshah.newartxassignment.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProfileRepositoryImpl(
    private val api: ApiService,
    private val db: AppDatabase,
    private val preferences: UserPreferencesRepository
) : ProfileRepository {

    override suspend fun getUserDetails(userId: Int): User = withContext(Dispatchers.IO) {
        db.postDao().getUserById(userId).let { entity ->
            User(entity.id, entity.firstName, entity.lastName, entity.email, entity.image)
        }
    }

    override fun getPostsFlow(userId: Int): Flow<List<Post>> {
        return db.postDao().getPostsByUser(userId).map { entities ->
            entities.map { Post(it.id, it.title, it.body, it.likes) }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun refreshPosts(userId: Int) = withContext(Dispatchers.IO) {
        val isOfflineMode = preferences.offlineModeFlow.first()
        if (isOfflineMode) {
            return@withContext
        }

        try {
            val response = api.getUserPosts(userId)
            val entities = response.posts.map { dto ->
                PostEntity(
                    id = dto.id,
                    userId = dto.userId,
                    title = dto.title,
                    body = dto.body,
                    likes = dto.reactions.likes
                )
            }
            db.postDao().insertPosts(entities)
            preferences.updateLastRefreshTime(System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}