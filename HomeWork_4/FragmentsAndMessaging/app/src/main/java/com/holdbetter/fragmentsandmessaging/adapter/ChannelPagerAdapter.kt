package com.holdbetter.fragmentsandmessaging.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.holdbetter.fragmentsandmessaging.fragment.ChannelsFragment
import com.holdbetter.fragmentsandmessaging.services.FragmentExtensions.chatRepository

class ChannelPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = arrayOf(
        ChannelsFragment.StreamCategoryFragment.newInstance(fragment.chatRepository.hashtagStreams),
        ChannelsFragment.StreamCategoryFragment.newInstance(fragment.chatRepository.hashtagStreams)
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}