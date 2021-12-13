package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel

import com.holdbetter.fintechchatproject.model.Stream

object ChannelModel {
    data class ChannelState(
        val lastSearchRequest: String? = null,
        val isDataLoaded: Boolean = false,
        val error: Throwable? = null
    )

    sealed class ChannelEffect {
        object DataNotEmpty : ChannelEffect()
        class ShowError(val error: Throwable): ChannelEffect()
    }

    sealed class ChannelCommand {
        object LoadStreams : ChannelCommand()
        object StartObservingSearchAvailability : ChannelCommand()
        object GetCachedStreams : ChannelCommand()

        class RunSearch(val searchRequest: String) : ChannelCommand()
        class PushDataToChild(val streamList: List<Stream>): ChannelCommand()
    }

    sealed class ChannelEvent {
        sealed class Ui : ChannelEvent() {
            class Searching(val searchRequest: String) : Ui()
            object Started : Ui()
            object Retry : Ui()
        }

        sealed class Internal : ChannelEvent() {
            object DataReady: Internal()
            class NoStreamsYet(val streamList: List<Stream>): Internal()
            class OnlineDataError(val error: Throwable) : Internal()

            class CacheReady(val streamList: List<Stream>) : Internal()
            class CacheEmpty(val streamList: List<Stream>) : Internal()
            class DbDataError(val error: Throwable) : Internal()

            object DataIsReadyForSearch : Internal()

            class SearchRequested(val request: String) : Internal()
        }
    }
}