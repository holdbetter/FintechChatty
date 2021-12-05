package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.viewmodel.PeopleViewModel
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.IUserViewer
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.UserNotFoundFragment
import com.holdbetter.fintechchatproject.databinding.UserDetailInstanceBinding
import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlin.properties.Delegates.notNull

class ProfileContent : Fragment(R.layout.user_detail_instance), IUserViewer {
    companion object {
        private const val USER_ID = "user"

        fun newInstance(userId: Long): ProfileContent {
            return ProfileContent().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    private var userId: Long by notNull()

    private val viewModel: PeopleViewModel by activityViewModels()
    private val compositeDisposable = CompositeDisposable()

    private val binding by viewBinding(UserDetailInstanceBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userId = requireArguments().getLong(DetailUserFragment.USER_ID)
        this.bind()
    }

    override fun bind() {
        startShimming()
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
            .into(binding.userImage)
    }

    override fun setUserName(name: String) {
        binding.userName.text = name
    }

    override fun startShimming() {
        with(binding) {
            profileContent.isVisible = false
            shimmer.root.isVisible = true

            shimmer.root.children.filter { it is ShimmerFrameLayout }
                .map { it as ShimmerFrameLayout }
                .forEach { it.startShimmer() }
        }
    }

    override fun stopShimming() {
        with(binding) {
            shimmer.root.children.filter { it is ShimmerFrameLayout }
                .map { it as ShimmerFrameLayout }
                .forEach { it.stopShimmer() }

            shimmer.root.isVisible = false
            profileContent.isVisible = true
        }
    }

    override fun setStatus(isOnline: Boolean, statusText: String) {
        with(binding) {
            userOnlineStatus.text = statusText
            userOnlineStatus.isEnabled = isOnline
        }
    }

    override fun handleError(throwable: Throwable) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                UserNotFoundFragment.newInstance(userId),
                UserNotFoundFragment::class.java.name
            )
            .commitAllowingStateLoss()
    }
}