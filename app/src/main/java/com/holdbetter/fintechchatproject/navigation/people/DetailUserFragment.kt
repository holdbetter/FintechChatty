package com.holdbetter.fintechchatproject.navigation.people

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.MaterialToolbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.navigation.profile.presenter.IUserPresenter
import com.holdbetter.fintechchatproject.navigation.profile.presenter.UserPresenter
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository
import com.holdbetter.fintechchatproject.navigation.profile.view.IUserViewer

class DetailUserFragment : Fragment(R.layout.fragment_user_detail), IUserViewer {
    companion object {
        const val USER_ID = "user"

        fun newInstance(userId: Int): DetailUserFragment {
            val bundle = Bundle()
            bundle.putInt(USER_ID, userId)
            return DetailUserFragment().apply {
                arguments = bundle
            }
        }
    }

    private var presenter: IUserPresenter? = null
    private var avatar: ImageView? = null
    private var nameView: TextView? = null
    private var statusView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userId = requireArguments().getInt(USER_ID)
        presenter = UserPresenter(userId, chatRepository, this)

        view.findViewById<MaterialToolbar>(R.id.profile_toolbar).apply {
            setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

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

    override fun setStatus(isOnline: Boolean, statusText: String) {
        this.statusView!!.text = statusText
        this.statusView!!.isEnabled = isOnline
    }
}