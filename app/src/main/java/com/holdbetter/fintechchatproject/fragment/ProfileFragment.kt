package com.holdbetter.fintechchatproject.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.presenter.IUserPresenter
import com.holdbetter.fintechchatproject.presenter.UserPresenter
import com.holdbetter.fintechchatproject.services.Util
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository
import com.holdbetter.fintechchatproject.view.IUserViewer

class ProfileFragment : Fragment(R.layout.fragment_profile), IUserViewer {
    companion object {
        fun newInstance(): ProfileFragment {
            val bundle = Bundle()
            return ProfileFragment().apply {
                arguments = bundle
            }
        }
    }

    private var presenter: IUserPresenter? = null
    private var avatar: ImageView? = null
    private var nameView: TextView? = null
    private var statusView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = UserPresenter(Util.currentUserId, chatRepository, this)

        avatar = view.findViewById(R.id.user_image)
        nameView = view.findViewById(R.id.user_name)
        statusView = view.findViewById(R.id.user_online_status)

        view.findViewById<MaterialButton>(R.id.log_out).setOnClickListener {
            Toast.makeText(it.context, "No action yet!", Toast.LENGTH_SHORT).show()
        }

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