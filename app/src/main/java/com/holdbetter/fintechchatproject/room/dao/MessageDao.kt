package com.holdbetter.fintechchatproject.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.holdbetter.fintechchatproject.domain.services.ModelMapper.toMessageWithReactions
import com.holdbetter.fintechchatproject.domain.services.ModelMapper.toSender
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.room.entity.MessageEntity
import com.holdbetter.fintechchatproject.room.entity.MessageWithReactions
import com.holdbetter.fintechchatproject.room.entity.ReactionEntity
import com.holdbetter.fintechchatproject.room.entity.SenderEntity
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface MessageDao {
    @Query("select * from messages where stream_id = :streamId order by date_in_seconds")
    fun getStreamMessages(streamId: Long): Maybe<List<MessageWithReactions>>

    @Query("select * from messages where stream_id = :streamId and topic_name = :topicName order by date_in_seconds")
    fun getTopicMessages(streamId: Long, topicName: String): Maybe<List<MessageWithReactions>>

    @Query("select * from senders where id = :senderId limit 1")
    fun getSender(senderId: Long): Single<SenderEntity>

    @Insert
    fun insertSender(sender: SenderEntity)

    @Query("delete from senders where id = :senderId")
    fun deleteSender(senderId: Long)

    @Transaction
    fun applySender(sender: SenderEntity) {
        deleteSender(sender.id)
        insertSender(sender)
    }

    fun applyTopicMessages(
        streamId: Long,
        topicName: String,
        messagesToCache: List<MessageItem.Message>
    ) {
        cleanTopicMessages(streamId, topicName)
        insertMessagesAndReactions(streamId, topicName, messagesToCache)
    }

    @Transaction
    fun insertMessagesAndReactions(
        streamId: Long,
        topicName: String,
        messagesToCache: List<MessageItem.Message>
    ) {
        for (message in messagesToCache) {
            applySender(message.toSender())
            val messageWithReactions = message.toMessageWithReactions(streamId, topicName)
            insertMessage(messageWithReactions.message, messageWithReactions.reactions)
        }
    }

    @Insert
    fun insertMessage(message: MessageEntity, reactions: List<ReactionEntity>)

    @Query("delete from messages where stream_id = :streamId and topic_name = :topicName")
    fun cleanTopicMessages(streamId: Long, topicName: String)
}