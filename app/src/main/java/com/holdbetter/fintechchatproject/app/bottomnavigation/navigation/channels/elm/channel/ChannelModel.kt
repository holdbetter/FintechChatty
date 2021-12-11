package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel

object ChannelModel {
    data class ChannelState(
        val lastSearchRequest: String? = null
    )

    sealed class ChannelEffect {
        object DataNotEmpty: ChannelEffect()
    }

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
            class SearchRequested(val request: String) : Internal()
        }
    }
}