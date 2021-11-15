package com.holdbetter.fintechchatproject.room.services

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import com.holdbetter.fintechchatproject.room.entity.TopicEntity

object DatabaseMapper {
    fun List<HashtagStreamEntity>.toHashtagStream(): List<HashtagStream> {
        return map {
            HashtagStream(
                it.id,
                it.name
            )
        }
    }

    fun List<TopicEntity>.toTopics(): List<Topic> {
        return map {
            Topic(
                it.maxId,
                it.name,
                it.streamId,
                it.streamName
            )
        }
    }
}