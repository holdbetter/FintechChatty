package com.holdbetter.fintechchatproject.app.bottomnavigation

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import by.kirich1409.viewbindingdelegate.viewBinding
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.ChannelsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.PeopleFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.ProfileFragment
import com.holdbetter.fintechchatproject.databinding.FragmentNavigationBinding

class NavigationFragment : Fragment(R.layout.fragment_navigation), INavigationAssistant {
    companion object {
        const val DEFAULT_BOTTOM_NAV_SELECTED_ID_KEY = "selectedItem"
        private const val CURRENT_PAGE_TAG_KEY = "currentPageTag"
        private const val CURRENT_PAGE_ID_KEY = "currentPageId"

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

    override val cachedPages: MutableMap<Int, Fragment> = mutableMapOf()

    override val pagesToBeAdded: Map<Int, Fragment> = mapOf(
        R.id.channels to ChannelsFragment.newInstance(),
        R.id.people to PeopleFragment.newInstance(),
        R.id.profile to ProfileFragment.newInstance()
    )

    override var currentFragmentPair: Pair<Int, Fragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            cachedPagesRestore(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            chatBottomNavigation.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.channels -> navigateTo(R.id.channels)
                    R.id.people -> navigateTo(R.id.people)
                    R.id.profile -> navigateTo(R.id.profile)
                }
                true
            }

            val arguments = requireArguments()
            val defaultSelectedItem = arguments.getInt(DEFAULT_BOTTOM_NAV_SELECTED_ID_KEY)
            val emptyDefaults = 0
            if (defaultSelectedItem != emptyDefaults) {
                chatBottomNavigation.selectedItemId = defaultSelectedItem
                arguments.clear()
            }
        }
    }

    override fun navigateTo(@IdRes fragmentMenuId: Int) {
        val transaction = childFragmentManager.beginTransaction()
        if (fragmentMenuId !in cachedPages) {
            addNewPage(fragmentMenuId, transaction)
        } else {
            attachCachedPage(fragmentMenuId, transaction)
        }.commitNowAllowingStateLoss()
    }

    private fun addNewPage(
        pageMenuId: Int,
        transaction: FragmentTransaction
    ): FragmentTransaction {
        val fragmentToBeAdded = pagesToBeAdded.getValue(pageMenuId)
        return transaction.apply {
            add(
                R.id.bottom_navigation_container,
                fragmentToBeAdded,
                fragmentToBeAdded::class.qualifiedName
            )
            currentFragmentPair?.let { detach(it.second) }
        }.runOnCommit {
            cachedPages[pageMenuId] = fragmentToBeAdded
            currentFragmentPair = pageMenuId to fragmentToBeAdded
        }
    }

    private fun attachCachedPage(
        pageMenuId: Int,
        transaction: FragmentTransaction
    ): FragmentTransaction {
        if (currentFragmentPair != cachedPages.getValue(pageMenuId)) {
            val fragmentToBeAttached = cachedPages.getValue(pageMenuId)
            return transaction.apply {
                detach(currentFragmentPair!!.second)
                attach(fragmentToBeAttached)
            }.runOnCommit { currentFragmentPair = pageMenuId to fragmentToBeAttached }
        }
        return transaction
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val (currentPageId, currentFragment) = currentFragmentPair!!
        outState.putString(CURRENT_PAGE_TAG_KEY, currentFragment::class.qualifiedName)
        outState.putInt(CURRENT_PAGE_ID_KEY, currentPageId)
    }

    private fun cachedPagesRestore(bundle: Bundle) {
        bundle.getString(CURRENT_PAGE_TAG_KEY)?.let {
            val lastOpenedFragment = childFragmentManager.findFragmentByTag(it)!!
            val lastOpenPageId = bundle.getInt(CURRENT_PAGE_ID_KEY)
            cachedPages[lastOpenPageId] = lastOpenedFragment
            currentFragmentPair = lastOpenPageId to lastOpenedFragment
        }
    }
}
