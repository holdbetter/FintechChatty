package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.SentMessageResponse
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toMessage
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.room.dao.MessageDao
import com.holdbetter.fintechchatproject.room.entity.MessageWithReactions
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toMessage
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toSender
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import java.util.concurrent.TimeUnit

class ChatRepository @AssistedInject constructor(
    private val messageDao: MessageDao,
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi,
    @Assisted("streamId") override val streamId: Long,
    @Assisted("topicName") override val topicName: String = "",
    @Assisted("originalEmojiList") override val originalEmojiList: List<EmojiApi>,
    @Assisted("meId") override val meId: Long,
) : IChatRepository {
    private val messageNarrow = Narrow.MessageNarrow(streamId, topicName).toJson()

    override fun getCachedMessages(): Maybe<List<MessageItem.Message>> {
        return messageDao.getTopicMessages(streamId, topicName)
            .flatMapObservable { Observable.fromIterable(it) }
            .concatMapEager { appendSender(it) }
            .toList()
            .toMaybe()
    }

    private fun appendSender(messageWithReactions: MessageWithReactions): Observable<MessageItem.Message> {
        return messageDao.getSender(messageWithReactions.message.senderId)
            .observeOn(Schedulers.computation())
            .map { messageWithReactions.toMessage(it.toSender()) }
            .toObservable()
    }

    override fun getFirstPortion(): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getNewestMessages(messageNarrow) }
            .map { it.toMessage() }
            .flatMap { isItLastPortion(it) }
            .doOnSuccess { cacheMessages(streamId, topicName, it.second) }
    }

    override fun cacheMessages(
        streamId: Long,
        topicName: String,
        messagesToCache: List<MessageItem.Message>
    ) {
        messageDao.applyTopicMessages(streamId, topicName, messagesToCache)
    }

    override fun getNextPortion(
        messageAnchorId: Long,
        currentMessages: List<MessageItem.Message>
    ): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getNextPortion(messageNarrow, messageAnchorId) }
            .map { it.toMessage() }
            .map { excludeAnchorMessage(messageAnchorId, it) }
            .map { appendWithChatMessages(it, currentMessages) }
            .flatMap { isItLastPortion(it) }
            .retryWhen { errors -> errors.flatMap { Flowable.timer(5000, TimeUnit.MILLISECONDS) } }
    }

    private fun isItLastPortion(lastMessages: List<MessageItem.Message>): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return api.getOldestMessage(messageNarrow)
            .map { it.messages.first().id }
            .map { id -> isMessagesContainsOldest(lastMessages, id) to lastMessages }
    }

    private fun isMessagesContainsOldest(
        messages: List<MessageItem.Message>,
        oldestMessageId: Long
    ): Boolean {
        return messages.any { message -> message.id == oldestMessageId }
    }

    private fun excludeAnchorMessage(
        messageAnchorId: Long,
        newPortion: List<MessageItem.Message>
    ): List<MessageItem.Message> {
        val mutablePortion = newPortion.toMutableList()
        mutablePortion.removeIf { it.id == messageAnchorId }
        return mutablePortion
    }

    private fun appendWithChatMessages(
        newPortion: List<MessageItem.Message>,
        chatMessages: List<MessageItem.Message>
    ): List<MessageItem.Message> {
        return listOf(
            *newPortion.toTypedArray(),
            *chatMessages.toTypedArray()
        )
    }

    override fun sendMessage(
        textMessage: String,
    ): Single<SentMessageResponse> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { it.sendMessage(textMessage, streamId, topicName) }
            .retryWhen { errors -> errors.flatMap { Flowable.timer(1000, TimeUnit.MILLISECONDS) } }
    }

    override fun sendReaction(
        messageId: Long,
        emojiNameToUpdate: String,
        currentMessages: List<MessageItem.Message>
    ): Maybe<List<MessageItem.Message>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .filter { !currentMessages.containsReaction(messageId, emojiNameToUpdate) }
            .flatMapSingle { sendReactionOnline(messageId, emojiNameToUpdate) }
            .flatMapSingle { getMessage(messageId) }
            .map { updatedMessage -> replaceMessage(messageId, updatedMessage, currentMessages) }
    }

    override fun removeReaction(
        messageId: Long,
        emojiNameToUpdate: String,
        currentMessages: List<MessageItem.Message>
    ): Single<List<MessageItem.Message>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { removeReactionOnline(messageId, emojiNameToUpdate) }
            .flatMap { getMessage(messageId) }
            .map { updatedMessage -> replaceMessage(messageId, updatedMessage, currentMessages) }
    }

    private fun replaceMessage(
        messageId: Long,
        updatedMessage: MessageItem.Message,
        currentMessages: List<MessageItem.Message>
    ): List<MessageItem.Message> {
        val mutableCurrentMessages = currentMessages.toMutableList()

        val oldMessage = mutableCurrentMessages.first { it.id == messageId }
        val oldMessageIndex = mutableCurrentMessages.indexOf(oldMessage)

        mutableCurrentMessages.remove(oldMessage)

        mutableCurrentMessages.add(oldMessageIndex, updatedMessage)
        return mutableCurrentMessages
    }

    private fun getMessage(messageId: Long): Single<MessageItem.Message> {
        return api.getMessage(messageNarrow, messageId)
            .map { it.toMessage().first() }
    }

    private fun sendReactionOnline(
        messageId: Long,
        emojiNameToUpdate: String
    ): Single<ResponseBody> {
        val (emojiName, emojiCode) = exchangeReaction(emojiNameToUpdate)
        return api.sendReaction(messageId, emojiName, emojiCode.lowercase())
    }

    private fun removeReactionOnline(
        messageId: Long,
        emojiNameToUpdate: String
    ): Single<ResponseBody> {
        val (emojiName, emojiCode) = exchangeReaction(emojiNameToUpdate)
        return api.removeReaction(messageId, emojiName, emojiCode)
    }

    private fun exchangeReaction(
        emojiNameToUpdate: String,
    ): EmojiApi {
        return originalEmojiList.find { it.emojiName == emojiNameToUpdate }
            ?: throw Exception("Couldn't exchange reaction to api emoji")
    }

    private fun List<MessageItem.Message>.containsReaction(
        messageId: Long,
        emojiNameToUpdate: String
    ): Boolean {
        return this.first { it.id == messageId }.reactions.any { it.userId == meId && it.emojiName == emojiNameToUpdate }
    }
}

@AssistedFactory
interface ChatRepositoryFactory {
    fun create(
        @Assisted("streamId") streamId: Long,
        @Assisted("topicName") topicName: String,
        @Assisted("originalEmojiList") originalEmojiList: List<EmojiApi>,
        @Assisted("meId") meId: Long
    ): ChatRepository
}