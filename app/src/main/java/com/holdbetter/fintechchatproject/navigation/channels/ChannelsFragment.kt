package com.holdbetter.fintechchatproject.navigation.channels

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.di.PocketDI
import com.holdbetter.fintechchatproject.navigation.channels.elm.channel.ChannelModel
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

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

    private var channelPager: ViewPager2? = null
    private var searchField: EditText? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.channel_tabs)
        channelPager = view.findViewById<ViewPager2>(R.id.channel_pager).apply {
            offscreenPageLimit = 2
            adapter = ChannelPagerAdapter(this@ChannelsFragment)
            currentItem = 1
        }

        TabLayoutMediator(tabLayout, channelPager!!) { tab, position ->
            when (position) {
                0 -> tab.text = "Subscribed"
                1 -> tab.text = "All streams"
            }
        }.attach()

        searchField = view.findViewById(R.id.stream_search_input)
        searchField!!.doOnTextChanged { input, _, _, _ ->
            if (!input.isNullOrBlank() && channelPager!!.currentItem != 1) {
                channelPager!!.currentItem = 1
            }

            store.accept(ChannelModel.ChannelEvent.Ui.Searching(input.toString()))
        }
    }

    override val initEvent: ChannelModel.ChannelEvent
        get() = ChannelModel.ChannelEvent.Ui.Started

    override fun createStore(): Store<ChannelModel.ChannelEvent, ChannelModel.ChannelEffect, ChannelModel.ChannelState> {
        return PocketDI.ChannelElmProvider.store.provide()
    }

    override fun render(state: ChannelModel.ChannelState) {
        searchField?.let {
            it.isEnabled = state.isReadyToSearch
        }
    }
}