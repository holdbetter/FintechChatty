package com.holdbetter.fintechchatproject.navigation.channels.view

import android.os.Bundle
import android.view.View
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.di.PocketDI
import com.holdbetter.fintechchatproject.navigation.channels.elm.AllStreamState
import com.holdbetter.fintechchatproject.navigation.channels.elm.StreamEffect
import com.holdbetter.fintechchatproject.navigation.channels.elm.StreamEvent
import vivid.money.elmslie.core.store.Store

class AllStreamsFragment :
    StreamFragment<StreamEvent, StreamEffect, AllStreamState>(
        StreamEvent.Ui.Started,
        R.layout.fragment_streams_sub_or_not
    ), IStreamFragment {
    companion object {
        private const val STREAMS_KEY = "streams"

        fun newInstance(): AllStreamsFragment {
            val bundle = Bundle()
            return AllStreamsFragment().apply {
                arguments = bundle
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.accept(StreamEvent.Ui.Started)
    }

    override val initEvent: StreamEvent
        get() = StreamEvent.Ui.Init

    override fun createStore(): Store<StreamEvent, StreamEffect, AllStreamState> {
        return PocketDI.StreamElmProvider.store.provide()
    }

    override fun render(state: AllStreamState) {
        shimming(state.isLoading)

        if (state.streamList.isNotEmpty()) {
            setStreams(state.streamList)
            store.accept(StreamEvent.Ui.DataReady)
        }
    }

    override fun handleEffect(effect: StreamEffect) {
        when (effect) {
            is StreamEffect.ShowDataError -> handleError(effect.error)
            is StreamEffect.ShowSearchedData -> setStreams(effect.streamList)
        }
    }

    fun storeProvider() = PocketDI.StreamElmProvider.store.provide()
}