package com.holdbetter.fintechchatproject.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.holdbetter.fintechchatproject.room.ChatDatabase

@Entity(tableName = ChatDatabase.STREAM_TABLE_NAME)
class StreamEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val subscribed: Boolean = false
) {
    fun partialCopy(isSubscribed: Boolean): StreamEntity {
        return StreamEntity(
            this.id,
            this.name,
            isSubscribed
        )
    }
}