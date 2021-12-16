package com.holdbetter.fintechchatproject.app.chat.elm

import com.holdbetter.fintechchatproject.domain.repository.IChatRepository
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor

class ChatActor @AssistedInject constructor(
    @Assisted("chatRepository") private val chatRepository: IChatRepository
) : Actor<ChatCommand, ChatEvent> {
    override fun execute(command: ChatCommand): Observable<ChatEvent> {
        return when (command) {
            is ChatCommand.FirstLoad -> chatRepository.getFirstPortion().mapEvents(
                successEventMapper = { messages -> ChatEvent.Internal.NewPortionLoaded(messages) },
                failureEventMapper = { error -> ChatEvent.Internal.LoadError(error) }
            )
            is ChatCommand.NextLoad -> {
                Observable.empty()
            }
            is ChatCommand.SendReaction -> chatRepository.sendReaction(command.messageId, command.emojiNameToUpdate).mapEvents(
                successEventMapper = { messages -> ChatEvent.Internal.ReactionUpdated(messages) },
                failureEventMapper = { error -> ChatEvent.Internal.LoadError(error) }
            )
            is ChatCommand.RemoveReaction -> chatRepository.removeReaction(command.messageId, command.emojiNameToUpdate).mapEvents(
                successEventMapper = { messages -> ChatEvent.Internal.ReactionUpdated(messages) },
                failureEventMapper = { error -> ChatEvent.Internal.LoadError(error) }
            )
            is ChatCommand.SendMessage -> chatRepository.sendMessage(command.messageText).mapEvents(
                successEventMapper = { messages -> ChatEvent.Internal.MessageAdded(messages) },
                failureEventMapper = { error -> ChatEvent.Internal.LoadError(error) }
            )
        }
    }
}

@AssistedFactory
interface ChatActorFactory {
    fun create(
        @Assisted("chatRepository") chatRepository: IChatRepository
    ): ChatActor
}
