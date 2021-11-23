package com.holdbetter.fintechchatproject.app.view

sealed class NavigationState {
    object CacheReady : NavigationState()
    object GoOnline : NavigationState()
}
