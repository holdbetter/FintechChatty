package com.holdbetter.fintechchatproject.app.chat.elm.stream

import com.holdbetter.fintechchatproject.app.chat.elm.ChatCommand
import com.holdbetter.fintechchatproject.app.chat.elm.ChatEvent
import com.holdbetter.fintechchatproject.domain.repository.IChatRepository
import com.holdbetter.fintechchatproject.room.services.UnexpectedRoomException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor

class StreamChatActor @AssistedInject constructor(
    @Assisted("chatRepository") private val chatRepository: IChatRepository
) : Actor<ChatCommand, ChatEvent> {
    override fun execute(command: ChatCommand): Observable<ChatEvent> {
        return when (command) {
            is ChatCommand.FirstLoad -> chatRepository.getFirstPortion().mapEvents(
                successEventMapper = { lastPortionAndMessages ->
                    val (isLastPortion, messages) = lastPortionAndMessages
                    ChatEvent.Internal.FirstPortionLoaded(isLastPortion, messages)
                },
                failureEventMapper = { error -> ChatEvent.Internal.FirstPortionLoadError(error) }
            )
            is ChatCommand.NextLoad -> chatRepository.getNextPortion(
                command.messageAnchorId,
                command.currentMessages
            ).mapEvents(
                successEventMapper = { lastPortionAndMessages ->
                    val (isLastPortion, messages) = lastPortionAndMessages
                    ChatEvent.Internal.NewPortionLoaded(isLastPortion, messages)
                },
                failureEventMapper = { error -> ChatEvent.Internal.FirstPortionLoadError(error) }
            )
            is ChatCommand.SendReaction -> chatRepository.sendReaction(
                command.messageId,
                command.emojiNameToUpdate,
                command.currentMessages
            ).mapEvents(
                successEventMapper = { messages -> ChatEvent.Internal.ReactionUpdated(messages) },
                completionEvent = ChatEvent.Internal.ReactionAlreadyAdded,
                failureEventMapper = { error -> ChatEvent.Internal.ReactionError(error) }
            )
            is ChatCommand.RemoveReaction -> chatRepository.removeReaction(
                command.messageId,
                command.emojiNameToUpdate,
                command.currentMessages
            ).mapEvents(
                successEventMapper = { messages -> ChatEvent.Internal.ReactionUpdated(messages) },
                failureEventMapper = { error -> ChatEvent.Internal.ReactionError(error) }
            )
            is ChatCommand.SendMessage -> chatRepository.sendMessage(command.messageText).mapEvents(
                successEvent = ChatEvent.Internal.MessageAdded,
                failureEventMapper = { error -> ChatEvent.Internal.OnlineLoadError(error) }
            )
            ChatCommand.GetCached -> chatRepository.getCachedMessages().mapEvents(
                successEventMapper = { messages -> ChatEvent.Internal.CacheLoaded(messages) },
                completionEvent = ChatEvent.Internal.CacheEmpty,
                failureEventMapper = { error ->
                    ChatEvent.Internal.CacheError(
                        UnexpectedRoomException(error)
                    )
                }
            )
        }
    }
}

@AssistedFactory
interface StreamChatActorFactory {
    fun create(
        @Assisted("chatRepository") chatRepository: IChatRepository
    ): StreamChatActor
}
