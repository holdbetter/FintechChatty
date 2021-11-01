package com.holdbetter.fintechchatproject.presenter

import com.holdbetter.fintechchatproject.model.*
import com.holdbetter.fintechchatproject.services.Util
import com.holdbetter.fintechchatproject.view.ITopicViewer
import java.util.ArrayList

class TopicPresenter(
    override val topicId: Int,
    private val chatRepository: IChatRepository,
    private val topicViewer: ITopicViewer,
) : ITopicPresenter {
    private val currentTopic: Topic by lazy {
        chatRepository.topics.find { t -> t.id == topicId }!!
    }

    override fun getTopicById(id: Int): Topic {
        return chatRepository.topics.find { t -> t.id == id }!!
    }

    private fun Topic.getMessageById(id: Int): Message {
        return messages.find { it.id == id }!!
    }

    private fun getReactionByCode(reactions: List<Reaction>, emojiCode: String): Reaction? {
        return reactions.find { it.emojiCode == emojiCode }
    }

    override fun getStreamById(id: Int): HashtagStream {
        return chatRepository.hashtagStreams.find { it.id == id }!!
    }

    override fun updateReactionsUsingDialog(messageId: Int, emojiCode: String) {
        val reactions = currentTopic.getMessageById(messageId).reactions

        // вообще, это синхронизированные операции должны быть
        // но это уже проблема бэка, земля им пухом
        val reaction = getReactionByCode(reactions, emojiCode)

        if (reaction != null) {
            if (!reaction.usersId.contains(Util.currentUserId)) {
                reaction.usersId.add(Util.currentUserId)
                topicViewer.onReactionUpdated(true, messageId)
            }
            topicViewer.onReactionUpdated(false, messageId)
        } else {
            reactions.add(
                Reaction(arrayListOf(Util.currentUserId), emojiCode)
            )
            topicViewer.onReactionUpdated(true, messageId)
        }
    }

    override fun updateReaction(messageId: Int, emojiCode: String) {
        val reactions = currentTopic.getMessageById(messageId).reactions
        val reaction = getReactionByCode(reactions, emojiCode)!!

        if (reaction.usersId.contains(Util.currentUserId)) {
            reaction.usersId.remove(Util.currentUserId)
            if (reaction.usersId.size == 0) {
                deleteReaction(reaction, reactions)
            }
        } else {
            reaction.usersId.add(Util.currentUserId)
        }

        topicViewer.onReactionUpdated(true, messageId)
    }

    private fun deleteReaction(reaction: Reaction, reactions: ArrayList<Reaction>) {
        reactions.remove(reaction)
    }

    override fun addMessage(messageText: String) {
        currentTopic.messages.add(
            Message(
                chatRepository.currentUser,
                messageText,
                topicId
            )
        )

        topicViewer.onMessageInserted(currentTopic.messages.lastIndex)
    }

    override fun bind() {
        val topic = currentTopic
        topicViewer.setMessages(topic.messages)

        val stream = getStreamById(topic.hashtagId)
        topicViewer.setHashtagTitle(stream.name)
        topicViewer.setTopicName("Topic: ${topic.name}")
    }

    override fun unbind() {

    }
}