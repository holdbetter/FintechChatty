package com.holdbetter.fintechchatproject.navigation.channels

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.navigation.channels.presenter.IStreamPresenter
import com.holdbetter.fintechchatproject.navigation.channels.presenter.StreamPresenter
import com.holdbetter.fintechchatproject.navigation.channels.view.IChannelViewer
import com.holdbetter.fintechchatproject.navigation.channels.view.StreamAdapter
import com.holdbetter.fintechchatproject.navigation.channels.view.StreamCategoryFragment
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository

class ChannelsFragment : Fragment(R.layout.fragment_channels), IChannelViewer {
    companion object {
        const val ALL_STREAMS_FRAGMENT_TAG = "f1"

        fun newInstance(): ChannelsFragment {
            val bundle = Bundle()
            return ChannelsFragment().apply {
                arguments = bundle
            }
        }
    }

    var streamPresenter: IStreamPresenter? = null

    var channelPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        streamPresenter = StreamPresenter(chatRepository, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.channel_tabs)
        channelPager = view.findViewById<ViewPager2>(R.id.channel_pager).apply {
            offscreenPageLimit = 2
        }
        channelPager!!.adapter = ChannelPagerAdapter(this)

        TabLayoutMediator(tabLayout, channelPager!!) { tab, position ->
            when (position) {
                0 -> tab.text = "Subscribed"
                1 -> tab.text = "All streams"
            }
        }.attach()

        val searchField = view.findViewById<EditText>(R.id.stream_search_input)
        searchField.doOnTextChanged { input, _, _, _ ->
            if (!input.isNullOrBlank() && channelPager!!.currentItem != 1) {
                channelPager!!.currentItem = 1
            }

            runSearchTask(input)
        }

        streamPresenter!!.bind()
    }

    private fun runSearchTask(input: CharSequence?) {
        Thread {
            if (childFragmentManager.findFragmentByTag("f1") != null) {
                streamPresenter!!.startSearch(input.toString())
            }
        }.start()
    }

    override fun onDestroyView() {
        streamPresenter!!.unbind()
        super.onDestroyView()
    }

    override fun startShimming() {
        val allStreamsFragment =
            childFragmentManager.findFragmentByTag(ALL_STREAMS_FRAGMENT_TAG) as? StreamCategoryFragment

        if (allStreamsFragment != null) {
            allStreamsFragment.shimmer!!.isVisible = true
            allStreamsFragment.shimmer!!.startShimmer()
            allStreamsFragment.streamsList!!.isVisible = false
        }
    }

    override fun stopShimming() {
        val allStreamsFragment =
            childFragmentManager.findFragmentByTag(ALL_STREAMS_FRAGMENT_TAG) as? StreamCategoryFragment

        if (allStreamsFragment != null) {
            allStreamsFragment.shimmer!!.stopShimmer()
            allStreamsFragment.shimmer!!.isVisible = false
            allStreamsFragment.streamsList!!.isVisible = true
        }
    }

    override fun setStreams(streams: MutableList<HashtagStream>) {
        val allStreamsFragment =
            childFragmentManager.findFragmentByTag(ALL_STREAMS_FRAGMENT_TAG) as? StreamCategoryFragment

        if (allStreamsFragment != null) {
            (allStreamsFragment
                .streamsList!!
                .adapter as StreamAdapter)
                .submitList(streams)
        }
    }
}