package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.ChannelsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.*
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class AllStreamsFragment : StreamFragment<AllStreamEffect, StreamState>(
    StreamEvent.Ui.Started
), IStreamFragment {
    companion object {
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
        (parentFragment as ChannelsFragment).channelComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (store.currentState.streamList == null) {
            store.accept(StreamEvent.Ui.Started)
        }
    }

    override fun createStore(): Store<StreamEvent, AllStreamEffect, StreamState> =
        allStreamElmProvider.provide()

    override fun render(state: StreamState) {
        shimming(state.isLoading)

        state.streamList?.let {
            binding.noStream.root.isVisible = it.isEmpty()
            binding.streamsList.isVisible = it.isNotEmpty()
            setStreams(it)
            store.accept(AllStreamEvent.Ui.DataWasSet)
        }
    }

    override fun handleEffect(effect: AllStreamEffect) {
        when (effect) {
            is AllStreamEffect.ShowSearchedData -> {
                with(effect.streamList) {
                    binding.noStream.root.isVisible = this.isEmpty()
                    binding.streamsList.isVisible = this.isNotEmpty()
                    setStreams(this)
                }
            }
        }
    }
}