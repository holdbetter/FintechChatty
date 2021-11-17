package com.holdbetter.fintechchatproject.main.view

sealed class NavigationState {
    object CacheReady : NavigationState()
    object GoOnline : NavigationState()
}
