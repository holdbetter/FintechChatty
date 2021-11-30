package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class ChannelStore @Inject constructor(private val actor: ChannelActor) {
    private val store by lazy {
        ElmStore(
            initialState = ChannelModel.ChannelState(isReadyToSearch = false),
            reducer = ChannelReducer(),
            actor = actor
        )
    }

    fun provide() = store
}