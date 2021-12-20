package com.holdbetter.fintechchatproject.room.services

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.model.*
import com.holdbetter.fintechchatproject.room.entity.*

object DatabaseMapper {
    fun MessageWithReactions.toMessage(sender: Sender): MessageItem.Message {
        return MessageItem.Message(
            this.message.id,
            sender,
            this.message.content,
            this.message.topicName,
            this.message.dateInSeconds,
            this.reactions.toReaction()
        )
    }

    fun SenderEntity.toSender(): Sender {
        return Sender(id, fullName, email, avatarUrl)
    }

    private fun List<ReactionEntity>.toReaction(): List<Reaction> {
        return map {
            Reaction(
                it.userId,
                it.emojiName,
                it.emojiCode,
                it.reactionType
            )
        }
    }

    fun List<StreamWithTopics>.toStream(): List<Stream> {
        return map {
            Stream(
                it.stream.id,
                it.stream.name,
                it.stream.subscribed,
                topics = it.topics.toTopics()
            )
        }
    }

    private fun List<TopicEntity>.toTopics(): List<Topic> {
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

    fun List<UserEntity>.toUserList(ignorePresence: Boolean = false): List<User> {
        return if (ignorePresence) {
            map { User(it.id, it.name, it.mail, it.avatarUrl, UserStatus.OFFLINE) }
        } else {
            map { it.toUser() }
        }
    }

    fun PersonalEntity.toUser(): User {
        return User(
            this.id,
            this.name,
            this.mail,
            this.avatarUrl,
            UserStatus.OFFLINE
        )
    }

    fun UserEntity.toUser(): User {
        return User(
            this.id,
            this.name,
            this.mail,
            this.avatarUrl,
            User.statusFromString(this.status)
        )
    }
}