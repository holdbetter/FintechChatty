package com.holdbetter.fintechchatproject.navigation.channels.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.di.PocketDI
import com.holdbetter.fintechchatproject.navigation.channels.elm.stream.StreamEvent
import com.holdbetter.fintechchatproject.navigation.channels.elm.stream.StreamState
import com.holdbetter.fintechchatproject.navigation.channels.elm.stream.SubbedStreamEffect
import vivid.money.elmslie.core.store.Store

class SubbedStreamsFragment : StreamFragment<SubbedStreamEffect, StreamState>(
    StreamEvent.Ui.Started,
    R.layout.fragment_streams_sub_or_not
), IStreamFragment {

    companion object {
        private const val STREAMS_KEY = "streams"

        fun newInstance(): SubbedStreamsFragment {
            return SubbedStreamsFragment().apply {
                arguments = bundleOf()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.accept(StreamEvent.Ui.Started)
    }

    override fun createStore(): Store<StreamEvent, SubbedStreamEffect, StreamState> = PocketDI.SubbedStreamElmProvider.store.provide()

    override fun render(state: StreamState) {
        shimming(state.isLoading)

        if (state.streamList.isNotEmpty()) {
            setStreams(state.streamList)
        }
    }
}