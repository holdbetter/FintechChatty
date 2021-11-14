package com.holdbetter.fintechchatproject.navigation.channels.view

import com.holdbetter.fintechchatproject.model.HashtagStream

sealed class StreamViewState {
    object Loading : StreamViewState()
    class Result(val streams: List<HashtagStream>) : StreamViewState()
    class Error(val error: Throwable) : StreamViewState()
    class CacheShowing(val streams: List<HashtagStream>) : StreamViewState()
}