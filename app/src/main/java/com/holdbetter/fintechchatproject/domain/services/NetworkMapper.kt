package com.holdbetter.fintechchatproject.domain.services

import com.holdbetter.fintechchatproject.domain.entity.*
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.model.Sender
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.entity.*

object NetworkMapper {
    fun StreamResponse.toStreamEntity(): List<StreamEntity> {
        return streams.map {
            StreamEntity(
                it.id,
                it.name,
            )
        }
    }

    fun SubbedStreamResponse.toStreamEntity(): List<StreamEntity> {
        return subscriptions.map {
            StreamEntity(
                it.id,
                it.name,
            )
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

    fun MessageResponse.toMessage(): List<MessageItem.Message> {
        return messages.map {
            MessageItem.Message(
                it.id,
                Sender(
                    it.senderID,
                    it.senderFullName,
                    it.senderEmail,
                    it.avatarURL
                ),
                it.content,
                it.subject,
                it.timestamp,
                it.userReactions.toReactions()
            )
        }
    }

    fun UserResponse.toPersonalEntity(): PersonalEntity {
        return PersonalEntity(
            this.userID,
            this.fullName,
            this.email,
            this.avatarURL,
        )
    }

    fun List<Member>.toUserEntity(): List<UserEntity> {
        return map { it.toUserEntity() }
    }

    fun Member.toUserEntity(): UserEntity {
        return UserEntity(
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
                it.value
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