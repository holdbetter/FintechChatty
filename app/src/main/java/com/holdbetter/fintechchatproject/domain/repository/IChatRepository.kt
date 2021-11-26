package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.SentMessageResponse
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.model.Message
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody

interface IChatRepository {
    fun getMessages(narrow: Narrow): Single<List<Message>>
    fun sendMessage(streamId: Long, topicName: String, textMessage: String): Single<SentMessageResponse>
    fun sendReaction(messageId: Long, emojiApi: EmojiApi): Single<ResponseBody>
    fun removeReaction(messageId: Long, emojiApi: EmojiApi): Single<ResponseBody>
}