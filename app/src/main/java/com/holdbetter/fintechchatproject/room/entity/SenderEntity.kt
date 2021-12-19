package com.holdbetter.fintechchatproject.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.holdbetter.fintechchatproject.room.ChatDatabase

@Entity(tableName = ChatDatabase.SENDERS_TABLE_NAME)
class SenderEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String
)