package com.aagamshah.newartxassignment.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aagamshah.newartxassignment.data.dao.PostDao
import com.aagamshah.newartxassignment.data.dao.UserDao
import com.aagamshah.newartxassignment.data.entity.PostEntity
import com.aagamshah.newartxassignment.data.entity.RemoteKeys
import com.aagamshah.newartxassignment.data.entity.UserEntity

@Database(entities = [UserEntity::class, RemoteKeys::class, PostEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}