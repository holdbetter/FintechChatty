package com.holdbetter.fintechchatproject.app.chat.view

import com.holdbetter.fintechchatproject.model.Message

interface IChatViewer {
    fun setMessages(messages: List<Message>)

    fun startLoading()
    fun stopLoading()
}
