package com.holdbetter.fintechchatproject.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.adapter.ChannelPagerAdapter
import com.holdbetter.fintechchatproject.adapter.StreamAdapter
import com.holdbetter.fintechchatproject.model.HashtagStream

class ChannelsFragment : Fragment(R.layout.fragment_channels) {
    companion object {
        fun newInstance(): ChannelsFragment {
            val bundle = Bundle()
            return ChannelsFragment().apply {
                arguments = bundle
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.channel_tabs)
        val channelPager = view.findViewById<ViewPager2>(R.id.channel_pager)
        channelPager.adapter = ChannelPagerAdapter(this)

        TabLayoutMediator(tabLayout, channelPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Subscribed"
                1 -> tab.text = "All streams"
            }
        }.attach()
    }

    class StreamCategoryFragment : Fragment(R.layout.fragment_streams_sub_or_not) {
        companion object {
            private const val STREAMS_KEY = "streams"

            fun newInstance(streams: ArrayList<HashtagStream>): StreamCategoryFragment {
                val bundle = Bundle()
                bundle.putParcelableArrayList(STREAMS_KEY, streams)
                return StreamCategoryFragment().apply {
                    arguments = bundle
                }
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val streamsList = view.findViewById<RecyclerView>(R.id.streams_list)

            streamsList.apply {
                addItemDecoration(DividerItemDecoration(view.context,
                    DividerItemDecoration.VERTICAL).apply {
                    setDrawable(ContextCompat.getDrawable(view.context,
                        R.drawable.streams_divider_decoration)!!)
                })

                val streams = requireArguments().getParcelableArrayList<HashtagStream>(
                    STREAMS_KEY)
                adapter = streams?.let { StreamAdapter(it) }
            }
        }
    }
}