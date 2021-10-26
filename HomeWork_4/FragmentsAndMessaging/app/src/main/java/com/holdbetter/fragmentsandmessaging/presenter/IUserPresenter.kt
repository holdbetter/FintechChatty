package com.holdbetter.fragmentsandmessaging.presenter

import com.holdbetter.fragmentsandmessaging.model.StupidUser

interface IUserPresenter {
    fun bind()
    fun unbind()
    fun getUserById(userId: Int): StupidUser
    fun getStatus(isOnline: Boolean): String
}