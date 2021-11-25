package com.holdbetter.fintechchatproject.navigation.channels.elm.stream

import vivid.money.elmslie.core.store.ElmStore

class SubbedStreamStore(private val actor: StreamActor) {
    private val store by lazy {
        ElmStore(
            initialState = StreamState(isLoading = false),
            reducer = SubbedStreamReducer(),
            actor = actor
        )
    }

    fun provide() = store
}