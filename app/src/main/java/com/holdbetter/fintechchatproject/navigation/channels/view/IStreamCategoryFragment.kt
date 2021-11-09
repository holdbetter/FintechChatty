package com.holdbetter.fintechchatproject.navigation.channels.view

import com.holdbetter.fintechchatproject.model.HashtagStream

interface IStreamCategoryFragment {
    fun startShimming()
    fun stopShimming()
    fun setStreams(streams: List<HashtagStream>)
}
