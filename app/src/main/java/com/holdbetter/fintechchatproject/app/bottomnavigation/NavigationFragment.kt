package com.holdbetter.fintechchatproject.app.bottomnavigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.ChannelsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.PeopleFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.ProfileFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.viewmodel.PersonalViewModel
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.viewmodel.PersonalViewModelFactory
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import javax.inject.Inject

class NavigationFragment : Fragment() {
    companion object {
        const val defaultBottomNavigationViewSelectedKey = "selectedItem"

        fun newInstance(defaultBottomNavigationViewSelectedId: Int): NavigationFragment {
            return NavigationFragment().apply {
                arguments = bundleOf(Pair(
                    defaultBottomNavigationViewSelectedKey,
                    defaultBottomNavigationViewSelectedId))
            }
        }
    }

    // initialize point for continuous sharing
    @Inject
    lateinit var personalViewModelFactory: PersonalViewModelFactory

    private var bottomNavigationView: BottomNavigationView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        app.appComponent.navigationComponent().create().inject(this)
        ViewModelProvider(requireActivity().viewModelStore, personalViewModelFactory).get(PersonalViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)
        bottomNavigationView = view.findViewById(R.id.chat_bottom_navigation)
        bottomNavigationView?.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.channels -> navigateToChannels()
                R.id.people -> navigateToPeople()
                R.id.profile -> navigateToProfile()
            }
            true
        }

        val arguments = requireArguments()
        val defaultSelectedItem = arguments.getInt(defaultBottomNavigationViewSelectedKey)
        if (defaultSelectedItem != 0) {
            bottomNavigationView?.selectedItemId = defaultSelectedItem
            arguments.clear()
        }

        return view
    }

    // мог сделать в один метод красиво через рефлексию, остановился,
    // когда студия предложила зависимость добавить
    private fun navigateToChannels() {
        if (childFragmentManager.findFragmentByTag(ChannelsFragment::class.java.name) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.bottom_navigation_container,
                    ChannelsFragment.newInstance(),
                    ChannelsFragment::class.java.name)
                .commit()
        }
    }

    private fun navigateToPeople() {
        if (childFragmentManager.findFragmentByTag(PeopleFragment::class.java.name) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.bottom_navigation_container,
                    PeopleFragment.newInstance(),
                    PeopleFragment::class.java.name)
                .commit()
        }
    }

    private fun navigateToProfile() {
        if (childFragmentManager.findFragmentByTag(ProfileFragment::class.java.name) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.bottom_navigation_container,
                    ProfileFragment.newInstance(),
                    ProfileFragment::class.java.name)
                .commit()
        }
    }
}