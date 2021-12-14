package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile

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
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.di.DaggerProfileComponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.di.ProfileComponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.elm.ProfileEffect
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.elm.ProfileEvent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.elm.ProfileState
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.elm.ProfileStore
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.IUserViewer
import com.holdbetter.fintechchatproject.databinding.FragmentProfileBinding
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.services.UnexpectedRoomException
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import com.holdbetter.fintechchatproject.services.FragmentExtensions.createStyledSnackbar
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class ProfileFragment :
    ElmFragment<ProfileEvent, ProfileEffect, ProfileState>(R.layout.fragment_profile), IUserViewer {
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment().apply {
                arguments = bundleOf()
            }
        }
    }

    private val binding by viewBinding(FragmentProfileBinding::bind)

    @Inject
    lateinit var profileElmProvider: ProfileStore

    private lateinit var profileComponent: ProfileComponent

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(app.appComponent) {
            profileComponent = DaggerProfileComponent.factory().create(
                androidDependencies = this,
                domainDependencies = this,
                repositoryDependencies = this
            )
        }

        profileComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            with(swipeToRefresh) {
                isEnabled = false
                setColorSchemeResources(com.holdbetter.fintechchatproject.R.color.white)
                setProgressBackgroundColorSchemeResource(com.holdbetter.fintechchatproject.R.color.green_accent)
                setOnRefreshListener {
                    swipeToRefresh.isEnabled = false
                    store.accept(ProfileEvent.Ui.Retry)
                }
            }

            if (store.currentState.user == null) {
                store.accept(ProfileEvent.Ui.Started)
            }
        }
    }

    override val initEvent: ProfileEvent
        get() = ProfileEvent.Ui.Init

    override fun createStore(): Store<ProfileEvent, ProfileEffect, ProfileState> {
        return profileElmProvider.provide()
    }

    override fun render(state: ProfileState) {
        shimming(state.isLoading)
        if (!state.isLoading) binding.swipeToRefresh.isRefreshing = false
        bindUser(state.user)
        cacheEmptyUi(state.isCacheEmpty)
    }

    private fun cacheEmptyUi(isCacheEmpty: Boolean) {
        with(binding) {
            cacheEmptyText.isVisible = isCacheEmpty
        }
    }

    override fun shimming(turnOn: Boolean) {
        with(binding) {
            shimmer.root.isVisible = turnOn
            shimmer.root.children.filter { it is ShimmerFrameLayout }
                .map { it as ShimmerFrameLayout }
                .forEach { if (turnOn) it.startShimmer() else it.stopShimmer() }
        }
    }

    override fun bindUser(user: User?) {
        with(binding) {
            if (user != null) {
                binding.userName.text = user.name

                Glide.with(this@ProfileFragment)
                    .load(user.avatarUrl)
                    .transform(CenterInside(), RoundedCorners(15))
                    .override(Target.SIZE_ORIGINAL)
                    .into(binding.userImage)

                profileContent.isVisible = true
                swipeToRefresh.isEnabled = true
            } else {
                profileContent.isVisible = false
            }
        }
    }

    override fun handleEffect(effect: ProfileEffect): Unit {
        return when (effect) {
            is ProfileEffect.ShowError -> handleError(effect.error)
        }
    }

    override fun handleError(error: Throwable) {
        val snackbar = createStyledSnackbar()
        when (error) {
            is UnexpectedRoomException -> {
                snackbar.setText(R.string.unexpected_room_exception)
            }
            is IOException, is NotConnectedException -> {
                snackbar.setText(R.string.no_connection)
                snackbar.duration = Snackbar.LENGTH_INDEFINITE
                snackbar.setAction(R.string.try_again) { store.accept(ProfileEvent.Ui.Retry) }
            }
            is TimeoutException -> {
                snackbar.setText(R.string.timeout_connection)
                snackbar.duration = Snackbar.LENGTH_INDEFINITE
                snackbar.setAction(R.string.try_again) { store.accept(ProfileEvent.Ui.Retry) }
            }
            else -> {
                snackbar.setText(R.string.undefined_error_message)
                snackbar.duration = Snackbar.LENGTH_INDEFINITE
                snackbar.setAction(R.string.reload) { store.accept(ProfileEvent.Ui.Retry) }
            }
        }

        snackbar.show()
    }
}