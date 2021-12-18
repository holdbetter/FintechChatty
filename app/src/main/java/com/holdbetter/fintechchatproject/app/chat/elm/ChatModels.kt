package com.holdbetter.fintechchatproject.app.chat.elm

import com.holdbetter.fintechchatproject.model.MessageItem

data class ChatState(
    val isLoading: Boolean = false,
    val isLastPortion: Boolean? = null,
    val messages: List<MessageItem.Message>? = null,
    val error: Throwable? = null
)

sealed class ChatEvent {
    sealed class Ui : ChatEvent() {
        object Init : Ui()
        object Started : Ui()

        class TopLimitEdgeReached(
            val messageAnchorId: Long,
            val currentMessages: List<MessageItem.Message>
        ) : Ui()

        class MessageSent(val textMessage: String) : Ui()

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
        class FirstPortionLoaded(val isLastPortion: Boolean, val messages: List<MessageItem.Message>): Internal()
        class NewPortionLoaded(val isLastPortion: Boolean, val messages: List<MessageItem.Message>) : Internal()

        class ReactionUpdated(val messages: List<MessageItem.Message>) : Internal()
        object MessageAdded : Internal()
        object ReactionAlreadyAdded : ChatEvent()

        class LoadError(val error: Throwable) : Internal()
    }
}

sealed class ChatCommand {
    object FirstLoad : ChatCommand()
    class NextLoad(val messageAnchorId: Long, val currentMessages: List<MessageItem.Message>) : ChatCommand()

    class SendMessage(val messageText: String) : ChatCommand()

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
    class ShowError(val error: Throwable) : ChatEffect()
}