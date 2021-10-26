package com.holdbetter.fintechchatproject.presenter

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.IChatRepository
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.view.ITopicViewer

class TopicPresenter(
    private val topicId: Int,
    private val chatRepository: IChatRepository,
    private val topicViewer: ITopicViewer,
) : ITopicPresenter {
    override fun getTopicById(id: Int): Topic {
        return chatRepository.topics.find { it.id == id }!!
    }

    override fun getStreamById(id: Int): HashtagStream {
        return chatRepository.hashtagStreams.find { it.id == id }!!
    }

    override fun bind() {
        val topic = getTopicById(topicId)
        topicViewer.setMessages(topic.messages)

        val stream = getStreamById(topic.hashtagId)
        topicViewer.setHashtagTitle(stream.name)
        topicViewer.setTopicName("Topic: ${topic.name}")
    }

    override fun unbind() {

    }
}