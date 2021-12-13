package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class SubbedStreamActor @Inject constructor(
    private val streamRepository: IStreamRepository,
) : Actor<SubbedStreamCommand, StreamEvent> {
    override fun execute(command: SubbedStreamCommand): Observable<StreamEvent> {
        return when (command) {
            SubbedStreamCommand.StartObserving -> streamRepository.startObservingStreams()
                .map { allStreams -> allStreams.filter { it.subscribed } }
                .mapSuccessEvent(
                    successEventMapper = { streamsList ->
                        StreamEvent.Internal.DataReceived(
                            streamsList
                        )
                    }
                )
        }
    }
}