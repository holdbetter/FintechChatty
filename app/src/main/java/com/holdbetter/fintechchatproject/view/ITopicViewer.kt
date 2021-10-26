package com.holdbetter.fintechchatproject.view

import com.holdbetter.fintechchatproject.model.Message

interface ITopicViewer {
    fun setMessages(messages: ArrayList<Message>)
    fun setTopicName(name: String)
    fun setHashtagTitle(hashtag: String)
}
