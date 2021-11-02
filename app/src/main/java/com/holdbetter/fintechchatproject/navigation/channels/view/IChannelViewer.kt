package com.holdbetter.fintechchatproject.navigation.channels.view

import com.holdbetter.fintechchatproject.model.HashtagStream

interface IChannelViewer {
    fun setStreams(streams: MutableList<HashtagStream>)
    fun startShimming()
    fun stopShimming()
}
