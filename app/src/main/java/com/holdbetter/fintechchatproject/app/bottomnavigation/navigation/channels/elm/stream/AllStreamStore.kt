package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class AllStreamStore @Inject constructor(private val actor: AllStreamActor, private val reducer: AllStreamReducer) {
    private val store by lazy {
        ElmStore(
            initialState = StreamState(isLoading = false),
            reducer = reducer,
            actor = actor
        )
    }

    fun provide() = store
}