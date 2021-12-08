package com.holdbetter.fintechchatproject.app.load.elm

import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class DataPrefetchActor @Inject constructor(
    private val emojiRepository: IEmojiRepository,
    private val personalRepository: IPersonalRepository
) : Actor<DataPrefetchCommand, DataPrefetchEvent> {
    override fun execute(command: DataPrefetchCommand): Observable<DataPrefetchEvent> {
        return when (command) {
            DataPrefetchCommand.Start -> emojiRepository.getAllEmojiOnline()
                .andThen(personalRepository.getMyselfOnline())
                .mapEvents(
                    successEvent = DataPrefetchEvent.Internal.Loaded,
                    failureEventMapper = { error -> DataPrefetchEvent.Internal.LoadingError(error) }
                )
        }
    }
}