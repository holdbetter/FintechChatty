package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class StreamActor @Inject constructor(
    private val streamRepository: IStreamRepository,
) : Actor<StreamCommand, StreamEvent> {
    override fun execute(command: StreamCommand): Observable<StreamEvent> {
        return when (command) {
            StreamCommand.LoadStreams -> streamRepository.getStreamsWithTopics()
                .filter { it.isNotEmpty() }
                .mapEvents(
                    successEventMapper = { streams -> StreamEvent.Internal.DataReady(streams) },
                    StreamEvent.Internal.CacheEmpty,
                    failureEventMapper = { error -> StreamEvent.Internal.DataLoadError(error) }
                )
            StreamCommand.GoOnlineForStreams -> streamRepository.getStreamsOnline()
                .mapEvents(
                    successEvent = StreamEvent.Internal.DataLoaded,
                    failureEventMapper = { error -> StreamEvent.Internal.DataLoadError(error) }
                )
            StreamCommand.DataIsAvailable -> {
                streamRepository.notifyParentsAboutDataAvailability()
                streamRepository.startHandleSearchRequests()
                    .mapSuccessEvent { streams -> AllStreamEvent.Internal.Searched(streams) }
            }
            StreamCommand.LoadSubbedStreams -> streamRepository.getStreamsWithTopics()
                .filter { it.isNotEmpty() }
                .mapEvents(
                    successEventMapper = { streams -> StreamEvent.Internal.DataReady(streams) },
                    StreamEvent.Internal.CacheEmpty,
                    failureEventMapper = { error -> StreamEvent.Internal.DataLoadError(error) }
                )
        }
    }
}