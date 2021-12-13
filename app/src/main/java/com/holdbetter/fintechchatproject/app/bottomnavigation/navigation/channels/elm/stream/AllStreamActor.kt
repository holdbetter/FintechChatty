package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class AllStreamActor @Inject constructor(
    private val streamRepository: IStreamRepository,
) : Actor<AllStreamCommand, StreamEvent> {
    override fun execute(command: AllStreamCommand): Observable<StreamEvent> {
        return when (command) {
            AllStreamCommand.DataIsAvailable -> {
                streamRepository.notifyParentsAboutDataAvailability()
                streamRepository.startHandleSearchResults()
                    .mapSuccessEvent { streams -> AllStreamEvent.Internal.Searched(streams) }
            }
            AllStreamCommand.StartObserving -> streamRepository.dataNotifier.mapSuccessEvent(
                successEventMapper = { streamsList -> StreamEvent.Internal.DataReceived(streamsList) }
            )
        }
    }
}