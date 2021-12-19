package com.holdbetter.fintechchatproject.app.chat.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class ChatReducer @Inject constructor() :
    DslReducer<ChatEvent, ChatState, ChatEffect, ChatCommand>() {
    override fun Result.reduce(event: ChatEvent): Any {
        return when (event) {
            ChatEvent.Ui.Init -> state { copy(isLoading = false, isLastPortion = null, isCachedData = null, messages = null, error = null) }
            is ChatEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +ChatCommand.FirstLoad }
            }
            is ChatEvent.Internal.FirstPortionLoaded -> state {
                copy(
                    isLoading = false,
                    isCachedData = false,
                    isLastPortion = event.isLastPortion,
                    messages = event.messages,
                    error = null
                )
            }
            is ChatEvent.Internal.FirstPortionLoadError -> commands { +ChatCommand.GetCached }
            is ChatEvent.Internal.OnlineLoadError -> commands { +ChatCommand.GetCached }
            ChatEvent.Internal.CacheEmpty -> state {
                copy(
                    isLoading = false,
                    isLastPortion = true,
                    isCachedData = true,
                    messages = emptyList(),
                    error = null
                )
            }
            is ChatEvent.Internal.CacheError -> {
                state {
                    copy(
                        isLoading = false,
                        isLastPortion = true,
                        isCachedData = true,
                        error = event.error
                    )
                }
                effects { +ChatEffect.CacheError(event.error) }
            }
            is ChatEvent.Internal.CacheLoaded -> state {
                copy(
                    isLoading = false,
                    isLastPortion = true,
                    isCachedData = true,
                    messages = event.messages,
                    error = null
                )
            }
            is ChatEvent.Internal.NewPortionLoaded -> state {
                copy(
                    isLastPortion = event.isLastPortion,
                    messages = event.messages
                )
            }
            is ChatEvent.Ui.ReactionSent -> {
                commands {
                    +ChatCommand.SendReaction(
                        event.messageId,
                        event.reaction,
                        event.currentMessages
                    )
                }
            }
            is ChatEvent.Ui.ReactionRemoved -> {
                commands {
                    +ChatCommand.RemoveReaction(
                        event.messageId,
                        event.reaction,
                        event.currentMessages
                    )
                }
            }
            ChatEvent.Ui.Retry -> {
                state {
                    copy(
                        isLoading = true,
                        isLastPortion = null,
                        messages = null,
                        error = null,
                        isCachedData = null
                    )
                }
                commands { +ChatCommand.FirstLoad }
            }
            is ChatEvent.Ui.MessageSent -> commands { +ChatCommand.SendMessage(event.textMessage) }
            is ChatEvent.Ui.TopLimitEdgeReached -> commands {
                +ChatCommand.NextLoad(
                    event.messageAnchorId,
                    event.currentMessages
                )
            }
            is ChatEvent.Internal.ReactionError -> effects { +ChatEffect.ReactionError(event.error) }
            is ChatEvent.Internal.MessageAdded -> commands { +ChatCommand.FirstLoad }
            is ChatEvent.Internal.ReactionUpdated -> state { copy(messages = event.messages) }
            ChatEvent.Internal.ReactionAlreadyAdded -> Any()
        }
    }
}