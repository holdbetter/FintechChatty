package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toMessage
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody

class ChatRepository @AssistedInject constructor(
    // TODO dao will be here
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi,
    @Assisted("streamId") override val streamId: Long,
    @Assisted("topicName") override val topicName: String = "",
    @Assisted("originalEmojiList") override val originalEmojiList: List<EmojiApi>
) : IChatRepository {

    private val messageNarrow = Narrow.MessageNarrow(streamId, topicName).toJson()

    override fun getFirstPortion(): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getNewestMessages(messageNarrow) }
            .map { it.toMessage() }
            .flatMap { isItLastPortion(it) }
    }

    private fun isItLastPortion(lastMessages: List<MessageItem.Message>): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return api.getOldestMessage(messageNarrow)
            .map { it.messages.first().id }
            .map { id -> isMessagesContainsOldest(lastMessages, id) to lastMessages }
    }

    private fun isMessagesContainsOldest(messages: List<MessageItem.Message>, oldestMessageId: Long): Boolean {
        val r = messages.any { message -> message.id == oldestMessageId }
        return r
    }

    override fun getNextPortion(messageAnchorId: Long, currentMessages: List<MessageItem.Message>): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getNextPortion(messageNarrow, messageAnchorId) }
            .map { it.toMessage() }
            .map { excludeAnchorMessage(messageAnchorId, it) }
            .map { appendWithChatMessages(it, currentMessages) }
            .flatMap { isItLastPortion(it) }
    }

    private fun excludeAnchorMessage(messageAnchorId: Long, newPortion: List<MessageItem.Message>): List<MessageItem.Message> {
        val mutablePortion = newPortion.toMutableList()
        mutablePortion.removeIf { it.id == messageAnchorId }
        return mutablePortion
    }

    private fun appendWithChatMessages(newPortion: List<MessageItem.Message>, chatMessages: List<MessageItem.Message>): List<MessageItem.Message> {
        return listOf(
            *newPortion.toTypedArray(),
            *chatMessages.toTypedArray()
        )
    }

    override fun sendMessage(
        textMessage: String,
    ): Single<List<MessageItem.Message>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { it.sendMessage(textMessage, streamId, topicName) }
            .flatMap { TODO() }
    }

    override fun sendReaction(
        messageId: Long,
        emojiNameToUpdate: String
    ): Single<List<MessageItem.Message>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { sendReactionOnline(messageId, emojiNameToUpdate) }
            .flatMap { TODO() }
    }

    override fun removeReaction(messageId: Long, emojiNameToUpdate: String): Single<List<MessageItem.Message>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { removeReactionOnline(messageId, emojiNameToUpdate) }
            .flatMap { TODO() }
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
}

@AssistedFactory
interface ChatRepositoryFactory {
    fun create(
        @Assisted("streamId") streamId: Long,
        @Assisted("topicName") topicName: String,
        @Assisted("originalEmojiList") originalEmojiList: List<EmojiApi>
    ): ChatRepository
}