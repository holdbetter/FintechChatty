package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.model.MessageItem
import io.reactivex.rxjava3.core.Single

interface IChatRepository: IRepository {
    val streamId: Long
    val topicName: String
    val originalEmojiList: List<EmojiApi>

    fun getFirstPortion(): Single<Pair<Boolean, List<MessageItem.Message>>>
    fun getNextPortion(messageAnchorId: Long, currentMessages: List<MessageItem.Message>): Single<Pair<Boolean, List<MessageItem.Message>>>

    fun sendMessage(textMessage: String): Single<List<MessageItem.Message>>
    fun sendReaction(messageId: Long, emojiNameToUpdate: String): Single<List<MessageItem.Message>>
    fun removeReaction(messageId: Long, emojiNameToUpdate: String): Single<List<MessageItem.Message>>
}