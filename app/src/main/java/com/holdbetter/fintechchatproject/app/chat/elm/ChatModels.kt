package com.holdbetter.fintechchatproject.app.chat.elm

import com.holdbetter.fintechchatproject.model.MessageItem

data class ChatState(
    val isLoading: Boolean = false,
    val isLastPortion: Boolean? = null,
    val messages: List<MessageItem.Message>? = null,
    val error: Throwable? = null,
    val isCachedData: Boolean? = null
)

sealed class ChatEvent {
    sealed class Ui : ChatEvent() {
        object Init : Ui()
        object Started : Ui()
        object Retry : Ui()

        class TopLimitEdgeReached(
            val messageAnchorId: Long,
            val currentMessages: List<MessageItem.Message>
        ) : Ui()

        class MessageSent(val textMessage: String, val topicName: String) : Ui()

        class ReactionSent(
            val messageId: Long,
            val reaction: String,
            val currentMessages: List<MessageItem.Message>
        ) : Ui()

        class ReactionRemoved(
            val messageId: Long,
            val reaction: String,
            val currentMessages: List<MessageItem.Message>
        ) : Ui()
    }

    sealed class Internal : ChatEvent() {
        class FirstPortionLoaded(
            val isLastPortion: Boolean,
            val messages: List<MessageItem.Message>
        ) : Internal()

        class NewPortionLoaded(
            val isLastPortion: Boolean,
            val messages: List<MessageItem.Message>
        ) : Internal()

        class CacheLoaded(val messages: List<MessageItem.Message>) : Internal()
        object CacheEmpty : Internal()
        class CacheError(val error: Throwable) : Internal()

        class ReactionUpdated(val messages: List<MessageItem.Message>) : Internal()
        object MessageAdded : Internal()
        object ReactionAlreadyAdded : ChatEvent()

        class ReactionError(val error: Throwable) : Internal()
        class FirstPortionLoadError(val error: Throwable) : Internal()
        class OnlineLoadError(val error: Throwable) : ChatEvent()
    }
}

sealed class ChatCommand {
    object FirstLoad : ChatCommand()
    class NextLoad(val messageAnchorId: Long, val currentMessages: List<MessageItem.Message>) :
        ChatCommand()

    object GetCached : ChatCommand()

    class SendMessage(val messageText: String, val topicName: String) : ChatCommand()

    class SendReaction(
        val messageId: Long,
        val emojiNameToUpdate: String,
        val currentMessages: List<MessageItem.Message>
    ) : ChatCommand()

    class RemoveReaction(
        val messageId: Long,
        val emojiNameToUpdate: String,
        val currentMessages: List<MessageItem.Message>
    ) : ChatCommand()
}

sealed class ChatEffect {
    class CacheError(val error: Throwable) : ChatEffect()
    class ReactionError(val error: Throwable) : ChatEffect()
}