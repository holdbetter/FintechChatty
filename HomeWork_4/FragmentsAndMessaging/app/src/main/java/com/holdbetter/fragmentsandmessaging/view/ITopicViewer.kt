package com.holdbetter.fragmentsandmessaging.view

import com.holdbetter.fragmentsandmessaging.model.Message

interface ITopicViewer {
    fun setMessages(messages: ArrayList<Message>)
    fun setTopicName(name: String)
    fun setHashtagTitle(hashtag: String)
}
