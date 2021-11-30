package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class SubbedStreamReducer : DslReducer<StreamEvent, StreamState, SubbedStreamEffect, StreamCommand>() {
    override fun Result.reduce(event: StreamEvent): Any {
        return when(event) {
            StreamEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +StreamCommand.LoadSubbedStreams }
            }
            StreamEvent.Internal.CacheEmpty -> {
                commands { +StreamCommand.GoOnlineForStreams }
            }
            StreamEvent.Internal.DataLoaded -> commands { +StreamCommand.LoadSubbedStreams }
            is StreamEvent.Internal.DataReady -> {
                state { copy(isLoading = false, streamList = event.streamList) }
            }
            is StreamEvent.Internal.DataLoadError -> {
                state { copy(isLoading = false, error = event.error) }
                effects { +SubbedStreamEffect.ShowDataError(event.error) }
            }
            StreamEvent.Ui.Init -> state { copy(isLoading = false) }
            is AllStreamEvent -> throw IllegalArgumentException("Unsupported event: ${event.javaClass.name}")
        }
    }
}