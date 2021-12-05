package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.*
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class AllStreamsFragment : StreamFragment<AllStreamEffect, StreamState>(
    StreamEvent.Ui.Started
), IStreamFragment {

    companion object {
        private const val STREAMS_KEY = "streams"

        fun newInstance(): AllStreamsFragment {
            return AllStreamsFragment().apply {
                arguments = bundleOf()
            }
        }
    }

    @Inject
    lateinit var allStreamElmProvider: AllStreamStore

    override fun onAttach(context: Context) {
        super.onAttach(context)

        app.appComponent.streamComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.accept(StreamEvent.Ui.Started)
    }

    override fun createStore(): Store<StreamEvent, AllStreamEffect, StreamState> = allStreamElmProvider.provide()

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
}