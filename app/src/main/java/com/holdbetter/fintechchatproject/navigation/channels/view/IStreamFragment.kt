package com.holdbetter.fintechchatproject.navigation.channels.view

import com.holdbetter.fintechchatproject.model.Stream

interface IStreamFragment {
    fun shimming(turnOn: Boolean)
    fun setStreams(streams: List<Stream>)
}
