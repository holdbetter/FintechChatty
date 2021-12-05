package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.StreamEvent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.StreamState
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.SubbedStreamEffect
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.SubbedStreamStore
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class SubbedStreamsFragment : StreamFragment<SubbedStreamEffect, StreamState>(
    StreamEvent.Ui.Started
), IStreamFragment {

    companion object {
        private const val STREAMS_KEY = "streams"

        fun newInstance(): SubbedStreamsFragment {
            return SubbedStreamsFragment().apply {
                arguments = bundleOf()
            }
        }
    }

    @Inject
    lateinit var subbedElmProvider: SubbedStreamStore

    override fun onAttach(context: Context) {
        super.onAttach(context)

        app.appComponent.streamComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.accept(StreamEvent.Ui.Started)
    }

    override fun createStore(): Store<StreamEvent, SubbedStreamEffect, StreamState> = subbedElmProvider.provide()

    override fun render(state: StreamState) {
        shimming(state.isLoading)

        if (state.streamList.isNotEmpty()) {
            setStreams(state.streamList)
        }
    }
}