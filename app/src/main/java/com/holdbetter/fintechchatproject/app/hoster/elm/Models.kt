package com.holdbetter.fintechchatproject.app.hoster.elm

data class InitializerCacheState(
    val error: Throwable? = null
)

sealed class InitializerCacheEvent {
    sealed class Ui : InitializerCacheEvent() {
        object Init : Ui()
    }

    sealed class Internal : InitializerCacheEvent() {
        object CacheEmpty : Internal()
        object CacheNotEmpty : Internal()
        class Error(val error: Throwable) : Internal()
    }
}

sealed class CacheCommand {
    object Initialize : CacheCommand()
}

sealed class CacheEffect {
    object ContinueWithCache: CacheEffect()
    object GoOnline: CacheEffect()
}