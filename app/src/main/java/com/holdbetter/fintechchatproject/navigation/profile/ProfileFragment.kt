package com.holdbetter.fintechchatproject.navigation.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.navigation.profile.view.IUserViewer
import com.holdbetter.fintechchatproject.navigation.profile.view.UserNotFoundFragment

class ProfileFragment : Fragment(R.layout.fragment_profile), IUserViewer {
    companion object {
        fun newInstance(): ProfileFragment {
            val bundle = Bundle()
            return ProfileFragment().apply {
                arguments = bundle
            }
        }
    }

//    private var presenter: IUserPresenter? = null
    private var shimmer: ConstraintLayout? = null
    private var content: ConstraintLayout? = null
    private var avatar: ImageView? = null
    private var nameView: TextView? = null
    private var statusView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        presenter = UserPresenter(Util.currentUserId, chatRepository, this)

        shimmer = view.findViewById(R.id.shimmer)
        content = view.findViewById(R.id.profile_content)
        avatar = view.findViewById(R.id.user_image)
        nameView = view.findViewById(R.id.user_name)
        statusView = view.findViewById(R.id.user_online_status)

        view.findViewById<MaterialButton>(R.id.log_out).setOnClickListener {
            Toast.makeText(it.context, "No action yet!", Toast.LENGTH_SHORT).show()
        }

//        presenter!!.bind()
        stopShimming()
    }

    override fun onDestroyView() {
//        presenter!!.unbind()
        super.onDestroyView()
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

    override fun setStatus(isOnline: Boolean, statusText: String) {
        this.statusView!!.text = statusText
        this.statusView!!.isEnabled = isOnline
    }

    override fun handleError(throwable: Throwable) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.bottom_navigation_container,
                UserNotFoundFragment.newInstance(),
                UserNotFoundFragment::class.java.name)
            .commitAllowingStateLoss()
    }
}