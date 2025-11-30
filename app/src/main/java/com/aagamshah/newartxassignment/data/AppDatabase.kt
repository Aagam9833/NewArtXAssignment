package com.aagamshah.newartxassignment.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aagamshah.newartxassignment.data.dao.UserDao
import com.aagamshah.newartxassignment.data.model.RemoteKeys
import com.aagamshah.newartxassignment.data.model.UserEntity

@Database(entities = [UserEntity::class, RemoteKeys::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}