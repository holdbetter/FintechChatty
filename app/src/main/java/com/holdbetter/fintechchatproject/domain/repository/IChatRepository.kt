package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.SentMessageResponse
import com.holdbetter.fintechchatproject.model.MessageItem
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface IChatRepository: IRepository {
    val streamId: Long
    val originalEmojiList: List<EmojiApi>
    val meId: Long

    val jsonNarrow: String

    fun getFirstPortion(): Single<Pair<Boolean, List<MessageItem.Message>>>
    fun getNextPortion(messageAnchorId: Long, currentMessages: List<MessageItem.Message>): Single<Pair<Boolean, List<MessageItem.Message>>>

    fun getCachedMessages(): Maybe<List<MessageItem.Message>>

    fun sendMessage(textMessage: String, topicName: String): Single<SentMessageResponse>
    fun sendReaction(messageId: Long, emojiNameToUpdate: String, currentMessages: List<MessageItem.Message>): Maybe<List<MessageItem.Message>>
    fun removeReaction(messageId: Long, emojiNameToUpdate: String, currentMessages: List<MessageItem.Message>): Single<List<MessageItem.Message>>
}