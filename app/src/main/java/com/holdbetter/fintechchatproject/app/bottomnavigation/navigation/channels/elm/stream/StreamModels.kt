package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import com.holdbetter.fintechchatproject.model.Stream

data class StreamState(
    val isLoading: Boolean,
    val streamList: List<Stream>? = null,
)

sealed class StreamEvent {
    sealed class Ui : StreamEvent() {
        object Init : Ui()
        object Started : Ui()
        object Refreshing : Ui()
    }

    sealed class Internal : StreamEvent() {
        class DataReceived(val streamsList: List<Stream>) : Internal()
    }
}

sealed class AllStreamEvent : StreamEvent() {
    sealed class Ui : AllStreamEvent() {
        object DataWasSet : Ui()
    }

    sealed class Internal : AllStreamEvent() {
        class Searched(val streamList: List<Stream>) : Internal()
    }
}

sealed class AllStreamEffect {
    class ShowSearchedData(val streamList: List<Stream>) : AllStreamEffect()
}

sealed class SubbedStreamEffect

sealed class SubbedStreamCommand {
    object StartObserving : SubbedStreamCommand()
}

sealed class AllStreamCommand {
    object StartObservingData : AllStreamCommand()
    object DataIsAvailable : AllStreamCommand()
    object ObserveSearching : AllStreamCommand()
}