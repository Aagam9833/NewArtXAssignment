package com.aagamshah.newartxassignment.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val userId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)