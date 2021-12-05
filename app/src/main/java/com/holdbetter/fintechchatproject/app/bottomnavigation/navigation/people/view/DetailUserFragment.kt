package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.databinding.FragmentUserDetailBinding

class DetailUserFragment : Fragment(R.layout.fragment_user_detail) {
    companion object {
        const val USER_ID = "user"

        fun newInstance(userId: Long): DetailUserFragment {
            return DetailUserFragment().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    private val binding by viewBinding(FragmentUserDetailBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userId = requireArguments().getLong(USER_ID)

        binding.profileToolbar.apply {
            setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        childFragmentManager.beginTransaction()
            .add(R.id.container, ProfileContent.newInstance(userId))
            .commitAllowingStateLoss()
    }
}