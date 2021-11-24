package com.holdbetter.fintechchatproject.navigation.channels.elm

import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor

class StreamActor(
    private val streamRepository: IStreamRepository
) : Actor<StreamCommand, StreamEvent> {
    override fun execute(command: StreamCommand): Observable<StreamEvent> {
        return when (command) {
            StreamCommand.LoadStreams -> streamRepository.getStreams()
                .filter { it.isNotEmpty() }
                .mapEvents(
                    successEventMapper = { streams -> StreamEvent.Internal.DataReady(streams) },
                    StreamEvent.Internal.CacheEmpty,
                    failureEventMapper = { error -> StreamEvent.Internal.DataLoadError(error) }
                )
            StreamCommand.GoOnline -> streamRepository.getStreamsOnline()
                .mapEvents(
                    successEvent = StreamEvent.Internal.DataLoaded,
                    failureEventMapper = { error -> StreamEvent.Internal.DataLoadError(error) }
                )
            StreamCommand.DataIsAvailable -> {
                streamRepository.notifyParentsAboutDataAvailability()
                streamRepository.startHandleSearchRequests()
                    .mapSuccessEvent { streams -> StreamEvent.Internal.Searched(streams) }
            }
        }
    }
}