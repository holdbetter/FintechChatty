package com.holdbetter.fintechchatproject.app.load.elm

import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.domain.repository.IRepository.Companion.TIMEOUT_MILLIS
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DataPrefetchActor @Inject constructor(
    private val emojiRepository: IEmojiRepository,
    private val personalRepository: IPersonalRepository
) : Actor<DataPrefetchCommand, DataPrefetchEvent> {
    override fun execute(command: DataPrefetchCommand): Observable<DataPrefetchEvent> {
        return when (command) {
            DataPrefetchCommand.Start -> emojiRepository.getAllEmojiOnline()
                .andThen(personalRepository.getMyselfOnline())
                .timeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .mapEvents(
                    successEvent = DataPrefetchEvent.Internal.Loaded,
                    failureEventMapper = { error -> DataPrefetchEvent.Internal.LoadingError(error) }
                )
        }
    }
}