package com.holdbetter.fintechchatproject.domain.services

import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.room.entity.MessageEntity
import com.holdbetter.fintechchatproject.room.entity.MessageWithReactions
import com.holdbetter.fintechchatproject.room.entity.ReactionEntity
import com.holdbetter.fintechchatproject.room.entity.SenderEntity

object ModelMapper {
    fun MessageItem.Message.toMessageWithReactions(streamId: Long, topicName: String): MessageWithReactions {
        return MessageWithReactions(
            MessageEntity(
                id,
                sender.id,
                streamId,
                topicName,
                messageContent,
                dateInSeconds
            ),
            reactions.toDbReactions(id)
        )
    }

    fun MessageItem.Message.toSender(): SenderEntity {
        return SenderEntity(
            this.sender.id,
            this.sender.fullName,
            this.sender.email,
            this.sender.avatarUrl
        )
    }

    private fun List<Reaction>.toDbReactions(messageId: Long): List<ReactionEntity> {
        return map {
            ReactionEntity(
                0,
                it.userId,
                it.emojiName,
                it.emojiCode,
                messageId,
                it.reactionType
            )
        }
    }
}