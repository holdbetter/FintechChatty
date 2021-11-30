package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class AllStreamReducer : DslReducer<StreamEvent, StreamState, AllStreamEffect, StreamCommand>() {
    override fun Result.reduce(event: StreamEvent): Any {
        return when (event) {
            StreamEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +StreamCommand.LoadStreams }
            }
            StreamEvent.Internal.CacheEmpty -> {
                commands { +StreamCommand.GoOnlineForStreams }
            }
            StreamEvent.Internal.DataLoaded -> commands { +StreamCommand.LoadStreams }
            is StreamEvent.Internal.DataReady -> {
                state { copy(isLoading = false, streamList = event.streamList) }
            }
            is StreamEvent.Internal.DataLoadError -> {
                state { copy(isLoading = false, error = event.error) }
                effects { +AllStreamEffect.ShowDataError(event.error) }
            }
            StreamEvent.Ui.Init -> state { copy(isLoading = false) }
            AllStreamEvent.Ui.DataReady -> commands { +StreamCommand.DataIsAvailable }
            is AllStreamEvent.Internal.Searched -> effects { +AllStreamEffect.ShowSearchedData(event.streamList) }
        }
    }
}