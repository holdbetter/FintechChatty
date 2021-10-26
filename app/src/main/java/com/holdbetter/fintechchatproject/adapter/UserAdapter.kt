package com.holdbetter.fintechchatproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holdbetter.fintechchatproject.MainActivity
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.fragment.DetailUserFragment
import com.holdbetter.fintechchatproject.model.StupidUser

class UserAdapter(private val users: List<StupidUser>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_instance, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount() = users.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.user_image)
        private val status: ImageView = itemView.findViewById(R.id.user_online_status)
        private val name: TextView = itemView.findViewById(R.id.user_name)
        private val mail: TextView = itemView.findViewById(R.id.user_mail)

        fun bind(user: StupidUser) {
            Glide.with(itemView)
                .load(user.avatarResourceId)
                .apply(RequestOptions().circleCrop())
                .into(avatar)

            status.isEnabled = user.isOnline
            name.text = user.name
            mail.text = user.mail

            itemView.setOnClickListener {
                navigateToUser(itemView.context, user)
            }
        }

        private fun navigateToUser(
            context: Context,
            user: StupidUser,
        ) {
            val mainActivity = context as MainActivity
            val detailUserFragment = DetailUserFragment.newInstance(user.id)

            mainActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_host_fragment, detailUserFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}