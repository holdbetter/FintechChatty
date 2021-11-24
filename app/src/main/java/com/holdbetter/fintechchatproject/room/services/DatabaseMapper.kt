package com.holdbetter.fintechchatproject.room.services

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import com.holdbetter.fintechchatproject.room.entity.TopicEntity

object DatabaseMapper {
    fun List<HashtagStreamEntity>.toHashtagStream(): List<Stream> {
        return map {
            Stream(
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

    fun List<ApiEmojiEntity>.toEmojiApiList(): List<EmojiApi> {
        return map {
            EmojiApi(
                it.emojiName,
                it.emojiCode
            )
        }
    }

    fun List<EmojiEntity>.toReactionList(): List<Reaction> {
        return map {
            Reaction(
                0,
                it.emojiName,
                it.emojiCode
            )
        }
    }
}