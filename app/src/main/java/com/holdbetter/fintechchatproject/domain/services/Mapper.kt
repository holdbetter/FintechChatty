package com.holdbetter.fintechchatproject.domain.services

import com.holdbetter.fintechchatproject.domain.entity.*
import com.holdbetter.fintechchatproject.model.*
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.Topic

object Mapper {
    fun StreamResponse.toHashtagStream(): MutableList<HashtagStream> {
        return ArrayList(streams.map {
            HashtagStream(
                it.id,
                it.name,
                arrayListOf()
            )
        })
    }

    fun TopicResponse.toTopics(streamId: Long, streamName: String): List<Topic> {
        return topics.map {
            Topic(it.maxId, it.name, streamId, streamName)
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