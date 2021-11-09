package com.holdbetter.fintechchatproject.navigation.profile.presenter

import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.core.Single

interface IUserPresenter {
    fun bind()
    fun unbind()
    fun getUserById(userId: Int): Single<User>
    fun getStatus(isOnline: Boolean): String
}