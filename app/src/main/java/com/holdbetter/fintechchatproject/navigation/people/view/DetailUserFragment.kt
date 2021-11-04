package com.holdbetter.fintechchatproject.navigation.people.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.holdbetter.fintechchatproject.R

class DetailUserFragment : Fragment(R.layout.fragment_user_detail) {
    companion object {
        const val USER_ID = "user"

        fun newInstance(userId: Int): DetailUserFragment {
            return DetailUserFragment().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userId = requireArguments().getInt(USER_ID)

        view.findViewById<MaterialToolbar>(R.id.profile_toolbar).apply {
            setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        childFragmentManager.beginTransaction()
            .add(R.id.container, ProfileContent.newInstance(userId))
            .commitAllowingStateLoss()
    }
}