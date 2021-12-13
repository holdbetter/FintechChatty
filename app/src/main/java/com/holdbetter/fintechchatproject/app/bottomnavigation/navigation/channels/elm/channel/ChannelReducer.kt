package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class ChannelReducer :
    DslReducer<ChannelModel.ChannelEvent, ChannelModel.ChannelState, ChannelModel.ChannelEffect, ChannelModel.ChannelCommand>() {
    override fun Result.reduce(event: ChannelModel.ChannelEvent): Any {
        return when (event) {
            ChannelModel.ChannelEvent.Ui.Started -> commands {
                +ChannelModel.ChannelCommand.StartObservingSearchAvailability
                +ChannelModel.ChannelCommand.LoadStreams
            }
            ChannelModel.ChannelEvent.Internal.DataIsReadyForSearch -> {
                state { copy(isDataLoaded = true) }
                effects { +ChannelModel.ChannelEffect.DataNotEmpty }
            }
            is ChannelModel.ChannelEvent.Ui.Searching -> commands {
                +ChannelModel.ChannelCommand.RunSearch(
                    event.searchRequest
                )
            }
            is ChannelModel.ChannelEvent.Internal.SearchRequested -> state {
                copy(
                    lastSearchRequest = event.request
                )
            }
            is ChannelModel.ChannelEvent.Internal.CacheReady -> {
                commands { +ChannelModel.ChannelCommand.PushDataToChild(event.streamList) }
            }
            ChannelModel.ChannelEvent.Internal.DataReady -> {
                commands { +ChannelModel.ChannelCommand.GetCachedStreams }
            }
            is ChannelModel.ChannelEvent.Internal.NoStreamsYet -> {
                commands { +ChannelModel.ChannelCommand.PushDataToChild(event.streamList) }
            }
            is ChannelModel.ChannelEvent.Internal.CacheEmpty -> commands {
                +ChannelModel.ChannelCommand.PushDataToChild(
                    event.streamList
                )
            }
            is ChannelModel.ChannelEvent.Internal.DbDataError -> effects {
                +ChannelModel.ChannelEffect.ShowError(
                    event.error
                )
            }
            is ChannelModel.ChannelEvent.Internal.OnlineDataError -> {
                commands { +ChannelModel.ChannelCommand.GetCachedStreams }
                effects { +ChannelModel.ChannelEffect.ShowError(event.error) }
            }
            ChannelModel.ChannelEvent.Ui.Retry -> commands { +ChannelModel.ChannelCommand.LoadStreams }
        }
    }
}