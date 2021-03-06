package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view.AllStreamsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view.SubbedStreamsFragment

class ChannelPagerAdapter(host: Fragment) : FragmentStateAdapter(host) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubbedStreamsFragment.newInstance()
            1 -> AllStreamsFragment.newInstance()
            else -> throw Exception("More than expected")
        }
    }
}