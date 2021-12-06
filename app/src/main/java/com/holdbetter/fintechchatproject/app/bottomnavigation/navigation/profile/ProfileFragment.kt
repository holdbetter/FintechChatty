package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.android.material.button.MaterialButton
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.viewmodel.PersonalViewModel
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.IUserViewer
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view.UserNotFoundFragment
import com.holdbetter.fintechchatproject.databinding.FragmentProfileBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

class ProfileFragment : Fragment(R.layout.fragment_profile), IUserViewer {
    companion object {
        fun newInstance(): ProfileFragment {
            val bundle = Bundle()
            return ProfileFragment().apply {
                arguments = bundle
            }
        }
    }

//    private val viewModel: PersonalViewModel by activityViewModels()
    private val binding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.logOut.setOnClickListener {
            Toast.makeText(it.context, "No action yet!", Toast.LENGTH_SHORT).show()
        }

//        this.bind()
    }

    override fun shimming(turnOn: Boolean) {
//        TODO("Not yet implemented")
    }

    override fun setImage(avatarUrl: String) {
        Glide.with(this)
            .load(avatarUrl)
            .transform(CenterInside(), RoundedCorners(15))
            .override(Target.SIZE_ORIGINAL)
            .into(binding.userImage)
    }

//    override fun bind() {
//        startShimming()
//        viewModel.getMyself()
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSuccess { stopShimming() }
//            .subscribeBy(
//                onSuccess = ::bindUser,
//                onError = ::handleError
//            ).addTo(compositeDisposable)
//    }

    private fun bindUser(user: User) {
        setUserName(user.name)
        setImage(user.avatarUrl)
    }

    override fun setUserName(name: String) {
        binding.userName.text = name
    }

    override fun setStatus(isOnline: Boolean, statusText: String) {
        with(binding) {
            userOnlineStatus.text = statusText
            userOnlineStatus.isEnabled = isOnline
        }
    }

    override fun handleError() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.bottom_navigation_container,
                UserNotFoundFragment.newInstance(),
                UserNotFoundFragment::class.java.name)
            .commitAllowingStateLoss()
    }
}