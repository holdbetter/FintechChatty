package com.holdbetter.fintechchatproject.navigation

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.holdbetter.fintechchatproject.navigation.channels.ChannelsFragment
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository

class ChannelPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = arrayOf(
        ChannelsFragment.StreamCategoryFragment.newInstance(fragment.chatRepository.hashtagStreams),
        ChannelsFragment.StreamCategoryFragment.newInstance(fragment.chatRepository.hashtagStreams)
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}