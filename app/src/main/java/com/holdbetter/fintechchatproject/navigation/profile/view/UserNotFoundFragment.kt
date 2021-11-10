package com.holdbetter.fintechchatproject.navigation.profile.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.navigation.people.view.ProfileContent
import com.holdbetter.fintechchatproject.navigation.profile.ProfileFragment

class UserNotFoundFragment : Fragment(R.layout.fragment_user_not_found) {
    companion object {
        private const val USER_ID = "user"

        fun newInstance(userId: Long = -1): UserNotFoundFragment {
            return UserNotFoundFragment().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    private var userId: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = requireArguments().getLong(USER_ID)

        val retry = view.findViewById<MaterialButton>(R.id.retry)
        retry.setOnClickListener { tryNavigateToUser() }
    }

    private fun tryNavigateToUser() {
        userId?.let {
            when (it) {
                -1L -> parentFragmentManager.beginTransaction()
                    .replace(R.id.bottom_navigation_container,
                        ProfileFragment.newInstance()).commit()
                else -> parentFragmentManager.beginTransaction()
                    .replace(R.id.container,
                        ProfileContent.newInstance(userId!!),
                        ProfileContent::class.java.name)
                    .commitAllowingStateLoss()
            }
        }
    }
}