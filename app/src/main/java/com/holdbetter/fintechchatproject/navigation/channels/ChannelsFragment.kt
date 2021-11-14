package com.holdbetter.fintechchatproject.navigation.channels

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.navigation.channels.view.IChannelViewer
import com.holdbetter.fintechchatproject.navigation.channels.viewmodel.StreamViewModel
import com.holdbetter.fintechchatproject.navigation.channels.viewmodel.StreamViewModelFactory
import com.holdbetter.fintechchatproject.services.FragmentExtensions.application

class ChannelsFragment : Fragment(R.layout.fragment_channels), IChannelViewer {
    companion object {
        fun newInstance(): ChannelsFragment {
            val bundle = Bundle()
            return ChannelsFragment().apply {
                arguments = bundle
            }
        }
    }

    private val viewModel: StreamViewModel by activityViewModels {
        StreamViewModelFactory(application.streamRepository, application.connectivityManager)
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

            viewModel.startSearch(input.toString())
        }

        viewModel.isAllStreamsAvailable.observe(viewLifecycleOwner, ::handleSearchAvailability)
    }

    override fun handleSearchAvailability(isAllStreamsAvailable: Boolean) {
        searchField?.let {
            it.isEnabled = isAllStreamsAvailable
        }
    }
}