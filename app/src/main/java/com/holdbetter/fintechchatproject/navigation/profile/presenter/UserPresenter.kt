package com.holdbetter.fintechchatproject.navigation.profile.presenter

import com.holdbetter.fintechchatproject.model.repository.IChatRepository
import com.holdbetter.fintechchatproject.model.StupidUser
import com.holdbetter.fintechchatproject.navigation.profile.view.IUserViewer

class UserPresenter(
    private val userId: Int,
    private val chatRepository: IChatRepository,
    private val userViewer: IUserViewer,
) : IUserPresenter {
    override fun getUserById(userId: Int): StupidUser {
        return chatRepository.users.find { it.id == userId }!!
    }

    override fun getStatus(isOnline: Boolean) = if (isOnline) "online" else "offline"

    override fun bind() {
        val user = getUserById(userId)
        userViewer.setImage(user.avatarResourceId)
        userViewer.setUserName(user.name)
        userViewer.setStatus(user.isOnline, getStatus(user.isOnline))
    }

    override fun unbind() {

    }
}