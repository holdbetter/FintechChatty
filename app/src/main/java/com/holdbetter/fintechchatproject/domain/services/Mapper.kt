package com.holdbetter.fintechchatproject.domain.services

import com.holdbetter.fintechchatproject.domain.entity.*
import com.holdbetter.fintechchatproject.model.*
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.Topic

object Mapper {
    fun StreamResponse.toHashtagStream(): MutableList<HashtagStream> {
        return ArrayList(streams.map {
            HashtagStream(
                it.streamID,
                it.name,
                arrayListOf()
            )
        })
    }

    fun TopicResponse.toTopics(streamId: Long): List<Topic> {
        return topics.map {
            Topic(it.maxId, it.name, streamId)
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
                it.reactions.toReactions()
            )
        }
    }

    fun UserResponse.toUser(): User {
        return User(
            this.userID.toLong(),
            this.fullName,
            this.email,
            this.avatarURL,
        )
    }

    private fun List<ReactionApi>.toReactions(): List<Reaction> {
        return filter { it.reactionType == Reaction.SUPPORTED_REACTION_TYPE }
            .map { it.toReaction() }
    }

    private fun ReactionApi.toReaction(): Reaction {
        return Reaction(
            this.userID,
            this.emojiCode,
            this.emojiName,
            this.reactionType
        )
    }
}