package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm.DetailUserEffect
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm.DetailUserEvent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm.DetailUserState
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm.DetailUserStore
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.IUserViewer
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.UserNotFoundFragment
import com.holdbetter.fintechchatproject.databinding.UserDetailInstanceBinding
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class ProfileContent : ElmFragment<DetailUserEvent, DetailUserEffect, DetailUserState>(R.layout.user_detail_instance), IUserViewer {
    companion object {
        private const val USER_ID = "user"

        fun newInstance(userId: Long): ProfileContent {
            return ProfileContent().apply {
                arguments = bundleOf(USER_ID to userId)
            }
        }
    }

    private var userId: Long by notNull()

    private val binding by viewBinding(UserDetailInstanceBinding::bind)

    @Inject
    lateinit var detailELmStore: DetailUserStore

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app.appComponent.detailUserComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userId = requireArguments().getLong(DetailUserFragment.USER_ID)
        store.accept(DetailUserEvent.Ui.Started(userId))
    }

    override val initEvent: DetailUserEvent
        get() = DetailUserEvent.Ui.Init

    override fun createStore(): Store<DetailUserEvent, DetailUserEffect, DetailUserState> {
        return detailELmStore.provide()
    }

    override fun render(state: DetailUserState) {
        shimming(state.isLoading)
        state.user?.let(::bindUser)
    }

    override fun handleEffect(effect: DetailUserEffect): Unit {
        return when(effect) {
            DetailUserEffect.ShowError -> handleError()
        }
    }

    private fun bindUser(user: User) {
        setUserName(user.name)
        setImage(user.avatarUrl)
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

    override fun shimming(turnOn: Boolean) {
        with(binding) {
            shimmer.root.isVisible = turnOn
            shimmer.root.children.filter { it is ShimmerFrameLayout }
                .map { it as ShimmerFrameLayout }
                .forEach { if (turnOn) it.startShimmer() else it.stopShimmer() }

            profileContent.isVisible = !turnOn
        }
    }

    override fun setStatus(isOnline: Boolean, statusText: String) {
        with(binding) {
            userOnlineStatus.text = statusText
            userOnlineStatus.isEnabled = isOnline
        }
    }

    override fun handleError() {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                UserNotFoundFragment.newInstance(userId),
                UserNotFoundFragment::class.java.name
            )
            .commitAllowingStateLoss()
    }
}