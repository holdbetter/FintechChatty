package com.holdbetter.fintechchatproject.navigation.channels.elm.channel

import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor

class ChannelActor(private val streamRepository: IStreamRepository) :
    Actor<ChannelModel.ChannelCommand, ChannelModel.ChannelEvent> {
    override fun execute(command: ChannelModel.ChannelCommand): Observable<ChannelModel.ChannelEvent> {
        return when (command) {
            ChannelModel.ChannelCommand.StartObservingCache -> streamRepository.dataAvailabilityNotifier.mapSuccessEvent {
                ChannelModel.ChannelEvent.Internal.DataNotEmpty
            }
            is ChannelModel.ChannelCommand.RunSearch -> {
                streamRepository.search(command.searchRequest)
                Observable.empty()
            }
        }
    }
}