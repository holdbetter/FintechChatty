package com.holdbetter.fintechchatproject.navigation.people

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.MaterialToolbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.navigation.people.view.ProfileContent
import com.holdbetter.fintechchatproject.navigation.profile.presenter.IUserPresenter
import com.holdbetter.fintechchatproject.navigation.profile.presenter.UserPresenter
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository
import com.holdbetter.fintechchatproject.navigation.profile.view.IUserViewer
import com.holdbetter.fintechchatproject.navigation.profile.view.UserNotFoundFragment

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