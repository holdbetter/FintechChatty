package com.holdbetter.fintechchatproject.app.chat.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class ChatReducer @Inject constructor() :
    DslReducer<ChatEvent, ChatState, ChatEffect, ChatCommand>() {
    override fun Result.reduce(event: ChatEvent): Any {
        return when (event) {
            ChatEvent.Ui.Init -> state { copy(isLoading = false, messages = null, error = null) }
            is ChatEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +ChatCommand.FirstLoad }
            }
            is ChatEvent.Ui.ReactionSent -> {
                commands { +ChatCommand.SendReaction(event.messageId, event.reaction) }
            }
            is ChatEvent.Ui.ReactionRemoved -> {
                commands { +ChatCommand.RemoveReaction(event.messageId, event.reaction) }
            }
            is ChatEvent.Ui.MessageSent -> commands { +ChatCommand.SendMessage(event.textMessage) }
            is ChatEvent.Ui.TopLimitEdgeReached -> commands { +ChatCommand.NextLoad(event.messageAnchorId, event.currentMessages) }
            is ChatEvent.Internal.LoadError -> effects { +ChatEffect.ShowError(event.error) }
            is ChatEvent.Internal.MessageAdded -> effects { +ChatEffect.MessageReceived(event.messages) }
            is ChatEvent.Internal.ReactionUpdated -> state { copy(messages = event.messages) }
            is ChatEvent.Internal.FirstPortionLoaded -> state { copy(isLoading = false, isLastPortion = event.isLastPortion, messages = event.messages) }
            is ChatEvent.Internal.NewPortionLoaded -> state { copy(isLoading = false, isLastPortion = event.isLastPortion, messages = event.messages) }
        }
    }
}