package com.holdbetter.fintechchatproject.chat.view

import com.holdbetter.fintechchatproject.model.Message

interface ITopicViewer {
    fun setMessages(messages: List<Message>)

    fun startLoading()
    fun stopLoading()

    fun onReactionUpdated(messageId: Int)
}
