package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import com.holdbetter.fintechchatproject.model.Stream

interface IStreamFragment {
    fun shimming(turnOn: Boolean)
    fun setStreams(streams: List<Stream>)
}
