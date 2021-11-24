package com.holdbetter.fintechchatproject.navigation.channels

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.holdbetter.fintechchatproject.navigation.channels.view.AllStreamsFragment

class ChannelPagerAdapter(host: Fragment) : FragmentStateAdapter(host) {
    override fun getItemCount() = 1
    override fun createFragment(position: Int): Fragment {
        return when (position) {
//            1 -> SubbedStreamsFragment.newInstance()
            0 -> AllStreamsFragment.newInstance()
            else -> throw Exception("More than expected")
        }
    }
}