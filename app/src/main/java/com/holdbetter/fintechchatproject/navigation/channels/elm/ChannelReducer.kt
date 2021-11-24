package com.holdbetter.fintechchatproject.navigation.channels.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class ChannelReducer : DslReducer<ChannelModel.ChannelEvent, ChannelModel.ChannelState, ChannelModel.ChannelEffect, ChannelModel.ChannelCommand>() {
    override fun Result.reduce(event: ChannelModel.ChannelEvent): Any {
        return when (event) {
            ChannelModel.ChannelEvent.Ui.Started -> commands { +ChannelModel.ChannelCommand.StartObservingCache }
            ChannelModel.ChannelEvent.Internal.DataNotEmpty -> state { copy(isReadyToSearch = true) }
            is ChannelModel.ChannelEvent.Ui.Searching -> commands { +ChannelModel.ChannelCommand.RunSearch(event.searchRequest) }
        }
    }
}