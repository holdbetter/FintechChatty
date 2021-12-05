package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.button.MaterialButton
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.ProfileContent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.ProfileFragment
import com.holdbetter.fintechchatproject.databinding.FragmentUserNotFoundBinding
import kotlin.properties.Delegates.notNull

class UserNotFoundFragment : Fragment(R.layout.fragment_user_not_found) {
    companion object {
        private const val USER_ID = "user"

        fun newInstance(userId: Long = -1): UserNotFoundFragment {
            return UserNotFoundFragment().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    private var userId: Long by notNull()

    private val binding by viewBinding(FragmentUserNotFoundBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = requireArguments().getLong(USER_ID)

        binding.retry.setOnClickListener { tryNavigateToUser() }
    }

    private fun tryNavigateToUser() {
        userId.let {
            when (it) {
                -1L -> parentFragmentManager.beginTransaction()
                    .replace(R.id.bottom_navigation_container,
                        ProfileFragment.newInstance()).commit()
                else -> parentFragmentManager.beginTransaction()
                    .replace(R.id.container,
                        ProfileContent.newInstance(userId),
                        ProfileContent::class.java.name)
                    .commitAllowingStateLoss()
            }
        }
    }
}