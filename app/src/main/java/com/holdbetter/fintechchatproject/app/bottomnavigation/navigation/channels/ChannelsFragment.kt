package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di.ChannelsComponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di.DaggerChannelsComponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel.ChannelModel
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel.ChannelStore
import com.holdbetter.fintechchatproject.databinding.FragmentChannelsBinding
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class ChannelsFragment :
    ElmFragment<ChannelModel.ChannelEvent,
            ChannelModel.ChannelEffect,
            ChannelModel.ChannelState>(R.layout.fragment_channels) {
    companion object {
        fun newInstance(): ChannelsFragment {
            val bundle = Bundle()
            return ChannelsFragment().apply {
                arguments = bundle
            }
        }
    }

    @Inject
    lateinit var channelsElmProvider: ChannelStore

    lateinit var channelComponent: ChannelsComponent

    private val binding by viewBinding(FragmentChannelsBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(app.appComponent) {
            channelComponent = DaggerChannelsComponent.factory().create(
                androidDependencies = this,
                domainDependencies = this,
                repositoryDependencies = this
            )
        }

        channelComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            with(channelPager) {
                offscreenPageLimit = 2
                adapter = ChannelPagerAdapter(this@ChannelsFragment)
            }

            TabLayoutMediator(channelTabs, channelPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.subscribed)
                    1 -> tab.text = getString(R.string.all_streams)
                }
            }.attach()

            streamSearchInput.doOnTextChanged { input, _, _, _ ->
                if (!input.isNullOrBlank() && channelPager.currentItem != 1) {
                    channelPager.currentItem = 1
                }

                store.accept(ChannelModel.ChannelEvent.Ui.Searching(input.toString()))
            }
        }
    }

    override val initEvent: ChannelModel.ChannelEvent
        get() = ChannelModel.ChannelEvent.Ui.Started

    override fun createStore(): Store<ChannelModel.ChannelEvent, ChannelModel.ChannelEffect, ChannelModel.ChannelState> =
        channelsElmProvider.provide()

    override fun render(state: ChannelModel.ChannelState) {}

    override fun handleEffect(effect: ChannelModel.ChannelEffect): Unit {
        return when (effect) {
            ChannelModel.ChannelEffect.DataNotEmpty -> enableSearch()
        }
    }

    private fun enableSearch() {
        with(binding) {
            streamSearchInput.isEnabled = true
            store.currentState.lastSearchRequest?.let {
                streamSearchInput.setText(it)
            }
        }
    }
}