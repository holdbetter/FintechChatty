package com.holdbetter.fintechchatproject.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.room.ChatDatabase

@Entity(tableName = ChatDatabase.REACTION_TABLE_NAME)
class ReactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "emoji_name") val emojiName: String,
    @ColumnInfo(name = "emoji_code") val emojiCode: String,
    @ColumnInfo(name = "message_id") val messageId: Long,
    @ColumnInfo(
        name = "reaction_type",
        defaultValue = Reaction.SUPPORTED_REACTION_TYPE
    ) val reactionType: String = Reaction.SUPPORTED_REACTION_TYPE,
)