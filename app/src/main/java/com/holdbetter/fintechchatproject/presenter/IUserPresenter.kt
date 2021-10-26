package com.holdbetter.fintechchatproject.presenter

import com.holdbetter.fintechchatproject.model.StupidUser

interface IUserPresenter {
    fun bind()
    fun unbind()
    fun getUserById(userId: Int): StupidUser
    fun getStatus(isOnline: Boolean): String
}