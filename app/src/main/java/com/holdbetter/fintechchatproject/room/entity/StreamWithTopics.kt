package com.holdbetter.fintechchatproject.room.entity

import androidx.room.Embedded
import androidx.room.Relation

class StreamWithTopics(
    @Embedded val stream: StreamEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "stream_id"
    )
    val topics: List<TopicEntity>
) {
    fun partialCopy(isSubscribed: Boolean): StreamWithTopics {
        return StreamWithTopics(
            this.stream.partialCopy(isSubscribed),
            this.topics
        )
    }
}