package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel

import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import com.holdbetter.fintechchatproject.room.services.UnexpectedRoomException
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class ChannelActor @Inject constructor(private val streamRepository: IStreamRepository) :
    Actor<ChannelModel.ChannelCommand, ChannelModel.ChannelEvent> {
    override fun execute(command: ChannelModel.ChannelCommand): Observable<ChannelModel.ChannelEvent> {
        return when (command) {
            ChannelModel.ChannelCommand.StartObservingSearchAvailability -> streamRepository.dataAvailabilityNotifier.mapSuccessEvent {
                ChannelModel.ChannelEvent.Internal.DataIsReadyForSearch
            }
            is ChannelModel.ChannelCommand.RunSearch -> streamRepository.search(command.searchRequest)
                .mapSuccessEvent {
                    ChannelModel.ChannelEvent.Internal.SearchRequested(it)
                }
            ChannelModel.ChannelCommand.LoadStreams -> streamRepository.getStreamsOnline()
                .mapEvents(
                    successEventMapper = { ChannelModel.ChannelEvent.Internal.DataReady },
                    completionEvent = ChannelModel.ChannelEvent.Internal.NoStreamsYet(emptyList()),
                    failureEventMapper = { error ->
                        ChannelModel.ChannelEvent.Internal.OnlineDataError(
                            error
                        )
                    }
                )
            ChannelModel.ChannelCommand.GetCachedStreams -> {
                streamRepository.getCachedStreams()
                    .mapEvents(
                        successEventMapper = { streamList ->
                            ChannelModel.ChannelEvent.Internal.CacheReady(
                                streamList
                            )
                        },
                        completionEvent = ChannelModel.ChannelEvent.Internal.CacheEmpty(emptyList()),
                        failureEventMapper = { error ->
                            ChannelModel.ChannelEvent.Internal.DbDataError(
                                UnexpectedRoomException(error)
                            )
                        }
                    )
            }
            is ChannelModel.ChannelCommand.PushDataToChild -> {
                streamRepository.pushStreams(command.streamList)
                Observable.empty()
            }
        }
    }
}