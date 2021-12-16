package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.SentMessageResponse
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.model.Message
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody

interface IChatRepository: IRepository {
    val streamId: Long
    val topicName: String
    val originalEmojiList: List<EmojiApi>

    fun getFirstPortion(): Single<List<Message>>
    fun sendMessage(textMessage: String): Single<List<Message>>
    fun sendReaction(messageId: Long, emojiNameToUpdate: String): Single<List<Message>>
    fun removeReaction(messageId: Long, emojiNameToUpdate: String): Single<List<Message>>
}