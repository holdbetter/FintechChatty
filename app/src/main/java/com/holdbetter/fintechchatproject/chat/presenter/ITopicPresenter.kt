package com.holdbetter.fintechchatproject.chat.presenter

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.Topic
import io.reactivex.rxjava3.core.Single

interface ITopicPresenter {
    val topicId: Int

    fun bind()
    fun unbind()
    fun getTopicById(id: Int): Single<Topic>
    fun getStreamById(id: Int): Single<HashtagStream>

    fun addReactionUsingDialog(messageId: Int, emojiCode: String)
    fun updateReaction(messageId: Int, emojiCode: String)

    fun addMessage(messageText: String)
}
