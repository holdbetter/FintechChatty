package com.holdbetter.fintechchatproject.app.chat.elm

import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.model.Message

data class ChatState(
    val isLoading: Boolean = false,
    val messages: List<Message>? = null,
    val error: Throwable? = null
)

sealed class ChatEvent {
    sealed class Ui : ChatEvent() {
        object Init : Ui()
        object Started : Ui()
        object Scrolled : Ui()

        class MessageSent(val textMessage: String) : Ui()

        class ReactionSent(
            val messageId: Long,
            val reaction: String,
        ) : Ui()

        class ReactionRemoved(
            val messageId: Long,
            val reaction: String,
        ) : Ui()
    }

    sealed class Internal : ChatEvent() {
        class NewPortionLoaded(val messages: List<Message>) : Internal()

        // class ReactionUpdated(val message: Message): Internal()
        class ReactionUpdated(val messages: List<Message>) : Internal()
        class MessageAdded(val messages: List<Message>) : Internal()

        class LoadError(val error: Throwable) : Internal()
    }
}

sealed class ChatCommand {
    object FirstLoad : ChatCommand()
    class NextLoad(val messageNarrow: Narrow.MessageNarrow) : ChatCommand()

    class SendMessage(val messageText: String) : ChatCommand()

    class SendReaction(
        val messageId: Long,
        val emojiNameToUpdate: String,
    ) : ChatCommand()

    class RemoveReaction(
        val messageId: Long,
        val emojiNameToUpdate: String
    ) : ChatCommand()
}

sealed class ChatEffect {
    class ShowError(val error: Throwable) : ChatEffect()
    class MessageReceived(val messages: List<Message>) : ChatEffect()
}