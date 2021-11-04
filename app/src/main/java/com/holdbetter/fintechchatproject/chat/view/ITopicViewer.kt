package com.holdbetter.fintechchatproject.chat.view

import com.holdbetter.fintechchatproject.model.Message

interface ITopicViewer {
    fun setMessages(messages: ArrayList<Message>)
    fun setTopicName(name: String)
    fun setHashtagTitle(hashtag: String)

    fun onReactionUpdated(messageId: Int)
    fun onMessageInserted(position: Int)
}
