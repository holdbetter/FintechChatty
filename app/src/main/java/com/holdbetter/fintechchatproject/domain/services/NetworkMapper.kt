package com.holdbetter.fintechchatproject.domain.services

import com.holdbetter.fintechchatproject.domain.entity.*
import com.holdbetter.fintechchatproject.model.*
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import com.holdbetter.fintechchatproject.room.entity.StreamEntity
import com.holdbetter.fintechchatproject.room.entity.TopicEntity

object NetworkMapper {
    fun StreamResponse.toHashtagStream(): MutableList<Stream> {
        return ArrayList(streams.map {
            Stream(
                it.id,
                it.name
            )
        })
    }

    fun StreamResponse.toHashtagStreamEntity(): List<StreamEntity> {
        return streams.map {
            StreamEntity(
                it.id,
                it.name
            )
        }
    }

    fun TopicResponse.toTopics(streamId: Long, streamName: String): List<Topic> {
        return topics.map {
            Topic(it.maxId, it.name, streamId, streamName)
        }
    }

    fun TopicResponse.toTopicEntity(streamId: Long, streamName: String): List<TopicEntity> {
        return topics.map {
            TopicEntity(
                0,
                it.maxId,
                it.name,
                streamId,
                streamName
            )
        }
    }

    fun MessageResponse.toMessage(): List<Message> {
        return messages.map {
            Message(
                it.id,
                Sender(
                    it.senderID,
                    it.senderFullName,
                    it.senderEmail,
                    it.avatarURL
                ),
                it.content,
                it.timestamp,
                it.userReactions.reversed().toReactions()
            )
        }
    }

    fun UserResponse.toUser(): User {
        return User(
            this.userID,
            this.fullName,
            this.email,
            this.avatarURL,
        )
    }

    fun Member.toUser(): User {
        return User(
            this.userID,
            this.fullName,
            this.email,
            this.avatarURL
        )
    }

    fun User.toSender(): Sender {
        return Sender(
            this.id,
            this.name,
            this.mail,
            this.avatarUrl
        )
    }

    fun EmojiListResponse.toApiEmojiEntityList(): List<ApiEmojiEntity> {
        return nameToCodepoint.map {
            ApiEmojiEntity(
                it.key,
                it.value
            )
        }
    }

    fun EmojiListResponse.toEmojiEntityList(): List<EmojiEntity> {
        return nameToCodepoint.map {
            EmojiEntity(
                it.key,
                it.value.uppercase().split('-')[0]
            )
        }
    }

    fun EmojiListResponse.toReactionList(): List<Reaction> {
        return nameToCodepoint.map {
            Reaction(
                0,
                it.key,
                it.value.uppercase().split('-')[0],
            )
        }
    }

    fun EmojiListResponse.toEmojiApiList(): List<EmojiApi> {
        return nameToCodepoint.map {
            EmojiApi(
                it.key,
                it.value.uppercase()
            )
        }
    }

    private fun List<UserReactionApi>.toReactions(): List<Reaction> {
        return filter { it.reactionType == Reaction.SUPPORTED_REACTION_TYPE }
            .map { it.toReaction() }
    }

    private fun UserReactionApi.toReaction(): Reaction {
        return Reaction(
            this.userID,
            this.emojiName,
            this.emojiCode.split('-')[0],
            this.reactionType
        )
    }
}