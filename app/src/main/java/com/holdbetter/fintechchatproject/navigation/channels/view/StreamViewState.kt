package com.holdbetter.fintechchatproject.navigation.channels.view

import com.holdbetter.fintechchatproject.model.Stream

sealed class StreamViewState {
    object Loading : StreamViewState()
    class Result(val streams: List<Stream>) : StreamViewState()
    class Error(val error: Throwable) : StreamViewState()
    class CacheShowing(val streams: List<Stream>) : StreamViewState()
}