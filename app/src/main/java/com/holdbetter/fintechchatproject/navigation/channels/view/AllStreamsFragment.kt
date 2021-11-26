package com.holdbetter.fintechchatproject.navigation.channels.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.di.PocketDI
import com.holdbetter.fintechchatproject.navigation.channels.elm.stream.AllStreamEffect
import com.holdbetter.fintechchatproject.navigation.channels.elm.stream.AllStreamEvent
import com.holdbetter.fintechchatproject.navigation.channels.elm.stream.StreamEvent
import com.holdbetter.fintechchatproject.navigation.channels.elm.stream.StreamState
import vivid.money.elmslie.core.store.Store

class AllStreamsFragment : StreamFragment<AllStreamEffect, StreamState>(
    StreamEvent.Ui.Started,
    R.layout.fragment_streams_sub_or_not
), IStreamFragment {

    companion object {
        private const val STREAMS_KEY = "streams"

        fun newInstance(): AllStreamsFragment {
            return AllStreamsFragment().apply {
                arguments = bundleOf()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.accept(StreamEvent.Ui.Started)
    }

    override fun createStore(): Store<StreamEvent, AllStreamEffect, StreamState> {
        return PocketDI.AllStreamElmProvider.store.provide()
    }

    override fun render(state: StreamState) {
        shimming(state.isLoading)

        if (state.streamList.isNotEmpty()) {
            setStreams(state.streamList)
            store.accept(AllStreamEvent.Ui.DataReady)
        }
    }

    override fun handleEffect(effect: AllStreamEffect) {
        when (effect) {
            is AllStreamEffect.ShowDataError -> handleError(effect.error)
            is AllStreamEffect.ShowSearchedData -> setStreams(effect.streamList)
        }
    }

    fun storeProvider() = PocketDI.AllStreamElmProvider.store.provide()
}