package com.holdbetter.fintechchatproject.navigation.people.view

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
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.navigation.profile.presenter.UserPresenter
import com.holdbetter.fintechchatproject.navigation.profile.view.IUserViewer
import com.holdbetter.fintechchatproject.navigation.profile.view.UserNotFoundFragment
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository

class ProfileContent : Fragment(R.layout.user_detail_instance), IUserViewer {
    companion object {
        private const val USER_ID = "user"

        fun newInstance(userId: Int): ProfileContent {
            return ProfileContent().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    private var content: ConstraintLayout? = null
    private var shimmer: ConstraintLayout? = null

    private var avatar: ImageView? = null
    private var nameView: TextView? = null
    private var statusView: TextView? = null

    private var presenter: UserPresenter? = null
    private var userId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userId = requireArguments().getInt(DetailUserFragment.USER_ID)
        presenter = UserPresenter(userId!!, chatRepository, this)

        shimmer = view.findViewById(R.id.shimmer)
        content = view.findViewById(R.id.profile_content)

        avatar = view.findViewById(R.id.user_image)
        nameView = view.findViewById(R.id.user_name)
        statusView = view.findViewById(R.id.user_online_status)

        presenter!!.bind()
    }

    override fun onDestroyView() {
        presenter!!.unbind()
        super.onDestroyView()
    }

    override fun setImage(resourceId: Int) {
        Glide.with(this)
            .load(resourceId)
            .transform(CenterInside(), RoundedCorners(15))
            .override(Target.SIZE_ORIGINAL)
            .into(avatar!!)
    }

    override fun setUserName(name: String) {
        this.nameView!!.text = name
    }

    override fun startShimming() {
        content!!.isVisible = false
        shimmer!!.isVisible = true

        shimmer!!.children.filter { it is ShimmerFrameLayout }
            .map { it as ShimmerFrameLayout }
            .forEach { it.startShimmer() }

    }

    override fun stopShimming() {
        shimmer!!.children.filter { it is ShimmerFrameLayout }
            .map { it as ShimmerFrameLayout }
            .forEach { it.stopShimmer() }

        shimmer!!.isVisible = false
        content!!.isVisible = true
    }

    override fun setStatus(isOnline: Boolean, statusText: String) {
        this.statusView!!.text = statusText
        this.statusView!!.isEnabled = isOnline
    }

    override fun handleError(throwable: Throwable) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container,
                UserNotFoundFragment.newInstance(userId!!),
                UserNotFoundFragment::class.java.name)
            .commitAllowingStateLoss()
    }
}