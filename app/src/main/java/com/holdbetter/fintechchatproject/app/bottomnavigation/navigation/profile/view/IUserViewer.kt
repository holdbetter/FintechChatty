package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view

interface IUserViewer {
    fun setImage(avatarUrl: String)
    fun setUserName(name: String)
    fun setStatus(isOnline: Boolean, statusText: String)

    fun startShimming()
    fun stopShimming()

    fun handleError(throwable: Throwable)

    fun bind()
    fun unbind()
}