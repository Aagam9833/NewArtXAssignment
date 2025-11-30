package com.aagamshah.newartxassignment.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val userId: Int,
    val prevKey: Int?, // Previous 'skip' offset
    val nextKey: Int?  // Next 'skip' offset
)