package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.databinding.UserListInstanceBinding
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.model.UserStatus
import com.holdbetter.fintechchatproject.model.UserStatus.*

class UserAdapter(val onUserClicked: (User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, UserDiffUtil())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            UserListInstanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onUserClicked
        )
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

    class UserViewHolder(
        private val binding: UserListInstanceBinding,
        private val onUserClicked: (User) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                Glide.with(root)
                    .load(user.avatarUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(userImage)

                userName.text = user.name
                userMail.text = user.mail
                userOnlineStatus.imageTintList = getStatusColor(user.status)

                root.setOnClickListener {
                    onUserClicked(user)
                }
            }
        }

        private fun getStatusColor(status: UserStatus): ColorStateList {
            val context = binding.root.context
            val resources = context.resources
            val theme = context.theme

            val colorInt =  when(status) {
                OFFLINE -> resources.getColor(R.color.user_offline_color, theme)
                IDLE -> resources.getColor(R.color.user_idle_color, theme)
                ACTIVE -> resources.getColor(R.color.user_active_color, theme)
            }

            return ColorStateList.valueOf(colorInt)
        }
    }
}