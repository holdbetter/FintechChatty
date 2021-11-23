package com.holdbetter.fintechchatproject.app.hoster.elm

import vivid.money.elmslie.core.store.ElmStore

class InitializerStore(private val actor: InitializeActor) {
    private val store by lazy {
        ElmStore(
            initialState = InitializerCacheState(),
            reducer = InitializeReducer(),
            actor = actor
        )
    }

    fun provide() = store
}