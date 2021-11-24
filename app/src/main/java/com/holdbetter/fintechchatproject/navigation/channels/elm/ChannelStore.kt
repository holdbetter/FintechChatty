package com.holdbetter.fintechchatproject.navigation.channels.elm

import vivid.money.elmslie.core.store.ElmStore

class ChannelStore(private val actor: ChannelActor) {
    private val store by lazy {
        ElmStore(
            initialState = ChannelModel.ChannelState(isReadyToSearch = false),
            reducer = ChannelReducer(),
            actor = actor
        )
    }

    fun provide() = store
}