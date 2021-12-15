package com.holdbetter.fintechchatproject.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.holdbetter.fintechchatproject.room.ChatDatabase

@Entity(tableName = ChatDatabase.USERS_TABLE_NAME)
class UserEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val mail: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(defaultValue = "idle") var status: String = "idle",
    @ColumnInfo(defaultValue = "0") val timestamp: Long = 0
)