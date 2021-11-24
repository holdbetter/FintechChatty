package com.holdbetter.fintechchatproject.navigation.channels.elm

import com.holdbetter.fintechchatproject.model.Stream

object ChannelModel {
    data class ChannelState(
        val isReadyToSearch: Boolean
    )

    object ChannelEffect
    sealed class ChannelCommand {
        object StartObservingCache : ChannelCommand()
        class RunSearch(val searchRequest: String) : ChannelCommand()
    }

    sealed class ChannelEvent {
        sealed class Ui : ChannelEvent() {
            class Searching(val searchRequest: String) : Ui()
            object Started : Ui()
        }

        sealed class Internal : ChannelEvent() {
            object DataNotEmpty : Internal()
        }
    }
}

data class AllStreamState(
    val isLoading: Boolean,
    val streamList: List<Stream> = emptyList(),
    val error: Throwable? = null
)

sealed class StreamEvent {
    sealed class Ui : StreamEvent() {
        object Init : Ui()
        object Started : Ui()
        object DataReady : Ui()
    }

    sealed class Internal : StreamEvent() {
        object CacheEmpty : Internal()
        object DataLoaded : Internal()
        class DataReady(val streamList: List<Stream>) : Internal()
        class DataLoadError(val error: Throwable) : Internal()
        class Searched(val streamList: List<Stream>) : StreamEvent()
    }
}

sealed class StreamEffect {
    class ShowDataError(val error: Throwable) : StreamEffect()
    class ShowSearchedData(val streamList: List<Stream>) : StreamEffect()
}

sealed class StreamCommand {
    object LoadStreams : StreamCommand()
    object GoOnline : StreamCommand()
    object DataIsAvailable : StreamCommand()
}