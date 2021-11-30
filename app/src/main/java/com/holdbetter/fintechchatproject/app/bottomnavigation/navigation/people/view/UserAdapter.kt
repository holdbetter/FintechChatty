package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.MainActivity
import com.holdbetter.fintechchatproject.model.User

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, UserDiffUtil())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_instance, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = asyncDiffer.currentList[position]
        holder.bind(user)
    }

    override fun getItemCount() = asyncDiffer.currentList.size

    fun submitList(users: List<User>) {
        asyncDiffer.submitList(users)
    }

    class UserDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.user_image)
        private val name: TextView = itemView.findViewById(R.id.user_name)
        private val mail: TextView = itemView.findViewById(R.id.user_mail)

        fun bind(user: User) {
            Glide.with(itemView)
                .load(user.avatarUrl)
                .apply(RequestOptions().circleCrop())
                .into(avatar)

            name.text = user.name
            mail.text = user.mail

            itemView.setOnClickListener {
                navigateToUser(itemView.context, user)
            }
        }

        private fun navigateToUser(
            context: Context,
            user: User,
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