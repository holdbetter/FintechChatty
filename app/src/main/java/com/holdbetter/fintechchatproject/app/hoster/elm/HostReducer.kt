package com.holdbetter.fintechchatproject.app.hoster.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class HostReducer : DslReducer<InitializerCacheEvent, InitializerCacheState, CacheEffect, CacheCommand>() {
    override fun Result.reduce(event: InitializerCacheEvent): Any {
        return when(event) {
            InitializerCacheEvent.Ui.Init -> {
                state { copy(error = null) }
                commands { +CacheCommand.Initialize }
            }
            InitializerCacheEvent.Internal.CacheEmpty -> effects { +CacheEffect.GoOnline }
            InitializerCacheEvent.Internal.CacheNotEmpty -> effects { +CacheEffect.ContinueWithCache }
            is InitializerCacheEvent.Internal.Error -> state { copy(error = event.error) }
        }
    }
}