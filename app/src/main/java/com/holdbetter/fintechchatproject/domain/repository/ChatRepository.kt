package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.SentMessageResponse
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toMessage
import com.holdbetter.fintechchatproject.model.Message
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import javax.inject.Inject

class ChatRepository @Inject constructor(override val api: TinkoffZulipApi) : IChatRepository {
    override fun getMessages(narrow: Narrow): Single<List<Message>> {
        val jsonNarrow = narrow.toJson()
        return api
            .getMessages(jsonNarrow)
            .subscribeOn(Schedulers.io())
            .map { it.toMessage() }
    }

    override fun sendMessage(
        streamId: Long,
        topicName: String,
        textMessage: String,
    ): Single<SentMessageResponse> {
        return api
            .sendMessage(textMessage, streamId, topicName)
            .subscribeOn(Schedulers.io())
    }

    override fun sendReaction(messageId: Long, emojiApi: EmojiApi): Single<ResponseBody> {
        return api
            .sendReaction(messageId, emojiApi.emojiName, emojiApi.emojiCode.lowercase())
            .subscribeOn(Schedulers.io())
    }

    override fun removeReaction(messageId: Long, emojiApi: EmojiApi): Single<ResponseBody> {
        return api
            .removeReaction(messageId, emojiApi.emojiName, emojiApi.emojiCode.lowercase())
            .subscribeOn(Schedulers.io())
    }
}