package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.SentMessageResponse
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toMessage
import com.holdbetter.fintechchatproject.model.Message
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

    override fun getFirstPortion(): Single<List<Message>> {
        val jsonNarrow = Narrow.MessageNarrow(streamId, topicName).toJson()
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getNewestMessages(jsonNarrow) }
            .map { it.toMessage() }
    }

    override fun sendMessage(
        textMessage: String,
    ): Single<List<Message>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { it.sendMessage(textMessage, streamId, topicName) }
            .flatMap { getFirstPortion() }
    }

    override fun sendReaction(
        messageId: Long,
        emojiNameToUpdate: String
    ): Single<List<Message>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { sendReactionOnline(messageId, emojiNameToUpdate) }
            .flatMap { getFirstPortion() }
    }

    override fun removeReaction(messageId: Long, emojiNameToUpdate: String): Single<List<Message>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { removeReactionOnline(messageId, emojiNameToUpdate) }
            .flatMap { getFirstPortion() }
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