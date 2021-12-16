package com.holdbetter.fintechchatproject.app.chat.view

import com.holdbetter.fintechchatproject.model.Message

interface IChatViewer {
    fun loading(turnOn: Boolean)
    fun setMessages(messages: List<Message>)
}
