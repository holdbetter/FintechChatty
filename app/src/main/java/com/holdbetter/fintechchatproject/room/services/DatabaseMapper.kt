package com.holdbetter.fintechchatproject.room.services

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.entity.*

object DatabaseMapper {
    fun List<StreamWithTopics>.toStream(): List<Stream> {
        return map {
            Stream(
                it.stream.id,
                it.stream.name,
                it.topics.map { topic -> Topic(
                    topic.maxId,
                    topic.name,
                    it.stream.id,
                    it.stream.name
                ) }
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

    fun List<UserEntity>.toUserList(): List<User> {
        return map { it.toUser() }
    }

    fun PersonalEntity.toUser(): User {
        return User(
            this.id,
            this.name,
            this.mail,
            this.avatarUrl
        )
    }

    fun UserEntity.toUser(): User {
        return User(
            this.id,
            this.name,
            this.mail,
            this.avatarUrl
        )
    }
}