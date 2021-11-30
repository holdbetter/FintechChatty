package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel.ChannelModel
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel.ChannelStore
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

    private var channelPager: ViewPager2? = null
    private var searchField: EditText? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        app.appComponent.channelsComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.channel_tabs)
        channelPager = view.findViewById<ViewPager2>(R.id.channel_pager).apply {
            offscreenPageLimit = 2
            adapter = ChannelPagerAdapter(this@ChannelsFragment)
        }

        TabLayoutMediator(tabLayout, channelPager!!) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.subscribed)
                1 -> tab.text = getString(R.string.all_streams)
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

    override fun createStore(): Store<ChannelModel.ChannelEvent, ChannelModel.ChannelEffect, ChannelModel.ChannelState> = channelsElmProvider.provide()

    override fun render(state: ChannelModel.ChannelState) {
        searchField?.let {
            it.isEnabled = state.isReadyToSearch
        }
    }
}