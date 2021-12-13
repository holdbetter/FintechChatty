package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class SubbedStreamReducer @Inject constructor() :
    DslReducer<StreamEvent, StreamState, SubbedStreamEffect, SubbedStreamCommand>() {
    override fun Result.reduce(event: StreamEvent): Any {
        return when (event) {
            StreamEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +SubbedStreamCommand.StartObserving }
            }
            StreamEvent.Ui.Init -> state { copy(isLoading = false) }
            is StreamEvent.Internal.DataReceived -> state {
                copy(
                    isLoading = false,
                    streamList = event.streamsList
                )
            }
            is AllStreamEvent -> throw IllegalArgumentException("Unsupported event: ${event.javaClass.name}")
        }
    }
}