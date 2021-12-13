package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.ChannelsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.StreamEvent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.StreamState
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.SubbedStreamEffect
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.SubbedStreamStore
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class SubbedStreamsFragment : StreamFragment<SubbedStreamEffect, StreamState>(
    StreamEvent.Ui.Started
), IStreamFragment {
    companion object {
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
        (parentFragment as ChannelsFragment).channelComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (store.currentState.streamList == null) {
            store.accept(StreamEvent.Ui.Started)
        }
    }

    override fun createStore(): Store<StreamEvent, SubbedStreamEffect, StreamState> =
        subbedElmProvider.provide()

    override fun render(state: StreamState) {
        shimming(state.isLoading)

        if (state.isLoading) binding.noStream.root.isVisible = false

        state.streamList?.let {
            binding.noStream.root.isVisible = it.isEmpty()
            binding.streamsList.isVisible = it.isNotEmpty()
            setStreams(it)
        }
    }
}