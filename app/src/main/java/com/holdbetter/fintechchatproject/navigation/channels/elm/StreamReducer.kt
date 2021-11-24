package com.holdbetter.fintechchatproject.navigation.channels.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class StreamReducer : DslReducer<StreamEvent, AllStreamState, StreamEffect, StreamCommand>() {
    override fun Result.reduce(event: StreamEvent): Any {
        return when(event) {
            StreamEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +StreamCommand.LoadStreams }
            }
            StreamEvent.Internal.CacheEmpty -> {
                commands { +StreamCommand.GoOnline }
            }
            StreamEvent.Internal.DataLoaded -> commands { +StreamCommand.LoadStreams }
            is StreamEvent.Internal.DataReady -> {
                state { copy(isLoading = false, streamList = event.streamList) }
            }
            is StreamEvent.Internal.DataLoadError -> {
                state { copy(isLoading = false, error = event.error) }
                effects { +StreamEffect.ShowDataError(event.error) }
            }
            StreamEvent.Ui.DataReady -> commands { +StreamCommand.DataIsAvailable }
            StreamEvent.Ui.Init -> state { copy(isLoading = false) }
            is StreamEvent.Internal.Searched -> effects { +StreamEffect.ShowSearchedData(event.streamList) }
        }
    }
}