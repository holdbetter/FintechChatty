package com.holdbetter.fintechchatproject.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.holdbetter.fintechchatproject.room.ChatDatabase

@Entity(tableName = ChatDatabase.EMOJI_TABLE_NAME)
class EmojiEntity(
    @PrimaryKey @ColumnInfo(name = "emoji_name") val emojiName: String,
    @ColumnInfo(name = "emoji_code") val emojiCode: String
)