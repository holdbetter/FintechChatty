package com.holdbetter.fintechchatproject.room.entity

import androidx.room.*
import com.holdbetter.fintechchatproject.room.ChatDatabase

@Entity(tableName = ChatDatabase.MESSAGE_TABLE_NAME)
class MessageEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "sender_id") val senderId: Long,
    @ColumnInfo(name = "stream_id") val streamId: Long,
    @ColumnInfo(name = "topic_name") val topicName: String,
    val content: String,
    @ColumnInfo(name = "date_in_seconds") val dateInSeconds: Long
)

class MessageWithReactions(
    @Embedded val message: MessageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "message_id",
    )
    val reactions: List<ReactionEntity>
)