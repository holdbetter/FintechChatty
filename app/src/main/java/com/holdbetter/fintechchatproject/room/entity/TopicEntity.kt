package com.holdbetter.fintechchatproject.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.ChatDatabase

@Entity(tableName = ChatDatabase.TOPIC_TABLE_NAME)
class TopicEntity(
    @ColumnInfo(name = "max_id") val maxId: Long,
    @PrimaryKey val name: String,
    @ColumnInfo(name = "stream_id") val streamId: Long,
    @ColumnInfo(name = "stream_name") val streamName: String,
    @ColumnInfo(defaultValue = Topic.TOPIC_DEFAULT_HEX) val color: String = Topic.TOPIC_DEFAULT_HEX
)