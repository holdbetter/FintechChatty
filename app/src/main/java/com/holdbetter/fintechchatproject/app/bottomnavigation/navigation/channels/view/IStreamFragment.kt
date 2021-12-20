package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.model.Topic

interface IStreamFragment {
    fun shimming(turnOn: Boolean)
    fun setStreams(streams: List<Stream>)
    fun onTopicClicked(context: Context, topic: Topic)
    fun onStreamClicked(context: Context, stream: Stream)
}
