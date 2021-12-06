package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view

interface IUserViewer {
    fun setImage(avatarUrl: String)
    fun setUserName(name: String)
    fun setStatus(isOnline: Boolean, statusText: String)

    fun shimming(turnOn: Boolean)

    fun handleError()
}