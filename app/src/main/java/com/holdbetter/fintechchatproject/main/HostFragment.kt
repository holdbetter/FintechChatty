package com.holdbetter.fintechchatproject.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.main.view.NavigationState
import com.holdbetter.fintechchatproject.main.viewmodel.EmojiViewModel
import com.holdbetter.fintechchatproject.main.viewmodel.EmojiViewModelFactory
import com.holdbetter.fintechchatproject.services.FragmentExtensions.application

class HostFragment : Fragment(R.layout.fragment_empty_hosting) {
    private val emojiViewModel: EmojiViewModel by activityViewModels {
        EmojiViewModelFactory(application.emojiRepository, application.connectivityManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emojiViewModel.getEmojiList()
        emojiViewModel.navigationState.observe(viewLifecycleOwner, ::handleState)
    }

    private fun handleState(state: NavigationState) {
        when (state) {
            NavigationState.CacheReady -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_host_fragment, NavigationFragment.newInstance(R.id.channels))
                    .commitAllowingStateLoss()
            }
            NavigationState.GoOnline -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_host_fragment, LoadingFragment.newInstance())
                    .commitAllowingStateLoss()
            }
        }
    }
}