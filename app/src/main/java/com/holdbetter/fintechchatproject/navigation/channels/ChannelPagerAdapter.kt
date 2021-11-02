package com.holdbetter.fintechchatproject.navigation.channels

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.holdbetter.fintechchatproject.navigation.channels.view.StreamCategoryFragment
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository

class ChannelPagerAdapter(private val host: Fragment) : FragmentStateAdapter(host) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int) =
        StreamCategoryFragment.newInstance(ArrayList(host.chatRepository.hashtagStreams))
}