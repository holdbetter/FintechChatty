package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class AllStreamReducer @Inject constructor(): DslReducer<StreamEvent, StreamState, AllStreamEffect, AllStreamCommand>() {
    override fun Result.reduce(event: StreamEvent): Any {
        return when (event) {
            StreamEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands {
                    +AllStreamCommand.StartObservingData
                }
            }
            StreamEvent.Ui.Init -> {
                state { copy(isLoading = false) }
                commands { +AllStreamCommand.ObserveSearching }
            }
            StreamEvent.Ui.Refreshing -> state { copy(isLoading = true, streamList = null) }
            AllStreamEvent.Ui.DataWasSet -> commands { +AllStreamCommand.DataIsAvailable }
            is AllStreamEvent.Internal.Searched -> effects { +AllStreamEffect.ShowSearchedData(event.streamList) }
            is StreamEvent.Internal.DataReceived -> {
                state { copy(isLoading = false, streamList = event.streamsList) }
            }
        }
    }
}