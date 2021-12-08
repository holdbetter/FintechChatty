package com.holdbetter.fintechchatproject.app.bottomnavigation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.ChannelsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.PeopleFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.ProfileFragment
import com.holdbetter.fintechchatproject.databinding.FragmentNavigationBinding
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app

class NavigationFragment : Fragment(R.layout.fragment_navigation) {
    companion object {
        const val DEFAULT_BOTTOM_NAV_SELECTED_ID_KEY = "selectedItem"

        fun newInstance(defaultBottomNavigationViewSelectedId: Int): NavigationFragment {
            return NavigationFragment().apply {
                arguments = bundleOf(
                    Pair(
                        DEFAULT_BOTTOM_NAV_SELECTED_ID_KEY,
                        defaultBottomNavigationViewSelectedId
                    )
                )
            }
        }
    }

    private val binding by viewBinding(FragmentNavigationBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        app.appComponent.navigationComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            chatBottomNavigation.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.channels -> navigateToChannels()
                    R.id.people -> navigateToPeople()
                    R.id.profile -> navigateToProfile()
                }
                true
            }

            val arguments = requireArguments()
            val defaultSelectedItem = arguments.getInt(DEFAULT_BOTTOM_NAV_SELECTED_ID_KEY)
            if (defaultSelectedItem != 0) {
                chatBottomNavigation.selectedItemId = defaultSelectedItem
                arguments.clear()
            }
        }
    }

    // мог сделать в один метод красиво через рефлексию, остановился,
    // когда студия предложила зависимость добавить
    private fun navigateToChannels() {
        if (childFragmentManager.findFragmentByTag(ChannelsFragment::class.java.name) == null) {
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.bottom_navigation_container,
                    ChannelsFragment.newInstance(),
                    ChannelsFragment::class.java.name
                )
                .commitAllowingStateLoss()
        }
    }

    private fun navigateToPeople() {
        if (childFragmentManager.findFragmentByTag(PeopleFragment::class.java.name) == null) {
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.bottom_navigation_container,
                    PeopleFragment.newInstance(),
                    PeopleFragment::class.java.name
                )
                .commitAllowingStateLoss()
        }
    }

    private fun navigateToProfile() {
        if (childFragmentManager.findFragmentByTag(ProfileFragment::class.java.name) == null) {
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.bottom_navigation_container,
                    ProfileFragment.newInstance(),
                    ProfileFragment::class.java.name
                )
                .commitAllowingStateLoss()
        }
    }
}