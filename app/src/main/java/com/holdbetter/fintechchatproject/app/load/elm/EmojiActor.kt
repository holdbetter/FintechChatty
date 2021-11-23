package com.holdbetter.fintechchatproject.app.load.elm

import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import vivid.money.elmslie.core.store.Actor

class EmojiActor(
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