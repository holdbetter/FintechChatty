package com.holdbetter.fintechchatproject.navigation.channels.elm.stream

import com.holdbetter.fintechchatproject.model.Stream

data class StreamState(
    val isLoading: Boolean,
    val streamList: List<Stream> = emptyList(),
    val error: Throwable? = null
)

sealed class StreamEvent {
    sealed class Ui : StreamEvent() {
        object Init : Ui()
        object Started : Ui()
    }

    sealed class Internal : StreamEvent() {
        object CacheEmpty : Internal()
        object DataLoaded : Internal()
        class DataReady(val streamList: List<Stream>) : Internal()
        class DataLoadError(val error: Throwable) : Internal()
    }
}

sealed class AllStreamEvent : StreamEvent() {
    sealed class Ui : AllStreamEvent() {
        object DataReady : Ui()
    }

    sealed class Internal : AllStreamEvent() {
        class Searched(val streamList: List<Stream>) : Internal()
    }
}

sealed class AllStreamEffect {
    class ShowDataError(val error: Throwable) : AllStreamEffect()
    class ShowSearchedData(val streamList: List<Stream>) : AllStreamEffect()
}

sealed class SubbedStreamEffect {
    class ShowDataError(val error: Throwable) : SubbedStreamEffect()
}

sealed class StreamCommand {
    object LoadStreams : StreamCommand()
    object GoOnline : StreamCommand()
    object DataIsAvailable : StreamCommand()
    object LoadSubbedStreams : StreamCommand()
}