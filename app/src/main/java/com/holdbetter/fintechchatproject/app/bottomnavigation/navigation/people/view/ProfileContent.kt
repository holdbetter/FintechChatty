package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.viewmodel.PeopleViewModel
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.IUserViewer
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.UserNotFoundFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

class ProfileContent : Fragment(R.layout.user_detail_instance), IUserViewer {
    companion object {
        private const val USER_ID = "user"

        fun newInstance(userId: Long): ProfileContent {
            return ProfileContent().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    private var userId: Long? = null
    private val viewModel: PeopleViewModel by activityViewModels()
    private val compositeDisposable = CompositeDisposable()

    private var content: ConstraintLayout? = null
    private var shimmer: ConstraintLayout? = null

    private var avatar: ImageView? = null
    private var nameView: TextView? = null
    private var statusView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userId = requireArguments().getLong(DetailUserFragment.USER_ID)

        shimmer = view.findViewById(R.id.shimmer)
        content = view.findViewById(R.id.profile_content)

        avatar = view.findViewById(R.id.user_image)
        nameView = view.findViewById(R.id.user_name)
        statusView = view.findViewById(R.id.user_online_status)

        this.bind()
    }

    override fun bind() {
        startShimming()
        val userId = userId!!
        viewModel.getUsersById(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { stopShimming() }
            .subscribeBy(
                onSuccess = ::bindUser,
                onError = ::handleError
            ).addTo(compositeDisposable)
    }

    private fun bindUser(user: User) {
        setUserName(user.name)
        setImage(user.avatarUrl)
    }

    override fun unbind() {
        compositeDisposable.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.unbind()
    }

    override fun setImage(avatarUrl: String) {
        Glide.with(this)
            .load(avatarUrl)
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