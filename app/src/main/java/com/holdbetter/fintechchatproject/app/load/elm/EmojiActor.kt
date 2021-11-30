package com.holdbetter.fintechchatproject.app.load.elm

import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class EmojiActor @Inject constructor(
    private val emojiRepository: IEmojiRepository,
) : Actor<EmojiLoadCommand, EmojiLoadEvent> {
    override fun execute(command: EmojiLoadCommand): Observable<EmojiLoadEvent> {
        return when (command) {
            EmojiLoadCommand.Start -> emojiRepository.getAllEmojiOnline()
                .mapEvents(
                    EmojiLoadEvent.Internal.Loaded
                ) { error -> EmojiLoadEvent.Internal.LoadingError(error) }
        }
    }
}