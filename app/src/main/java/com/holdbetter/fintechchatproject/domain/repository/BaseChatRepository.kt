package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toMessage
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody

abstract class BaseChatRepository(
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi,
) : IChatRepository {

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
            .flatMapSingle { getMessage(jsonNarrow, messageId) }
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
            .flatMap { getMessage(jsonNarrow, messageId) }
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

    fun sendReactionOnline(
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

    private fun getMessage(jsonNarrow: String, messageId: Long): Single<MessageItem.Message> {
        return api.getMessage(jsonNarrow, messageId)
            .map { it.toMessage().first() }
    }

    fun appendWithChatMessages(
        newPortion: List<MessageItem.Message>,
        chatMessages: List<MessageItem.Message>
    ): List<MessageItem.Message> {
        return listOf(
            *newPortion.toTypedArray(),
            *chatMessages.toTypedArray()
        )
    }

    fun excludeAnchorMessage(
        messageAnchorId: Long,
        newPortion: List<MessageItem.Message>
    ): List<MessageItem.Message> {
        val mutablePortion = newPortion.toMutableList()
        mutablePortion.removeIf { it.id == messageAnchorId }
        return mutablePortion
    }

    fun isItLastPortion(lastMessages: List<MessageItem.Message>): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return api.getOldestMessage(jsonNarrow)
            .map { it.messages.first().id }
            .map { id -> isMessagesContainsOldest(lastMessages, id) to lastMessages }
    }

    private fun isMessagesContainsOldest(
        messages: List<MessageItem.Message>,
        oldestMessageId: Long
    ): Boolean {
        return messages.any { message -> message.id == oldestMessageId }
    }
}