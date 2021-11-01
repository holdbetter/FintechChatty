package com.holdbetter.fintechchatproject.presenter

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.Topic

interface ITopicPresenter {
    val topicId: Int

    fun bind()
    fun unbind()
    fun getTopicById(id: Int): Topic
    fun getStreamById(id: Int): HashtagStream

    fun updateReactionsUsingDialog(messageId: Int, emojiCode: String)
    fun updateReaction(messageId: Int, emojiCode: String)

    fun addMessage(messageText: String)
}
