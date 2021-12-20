package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.model.MessageItem

interface ITopicChatRepository: IRepository {
    val topicName: String

    fun cacheMessages(streamId: Long, topicName: String, messagesToCache: List<MessageItem.Message>)
}