package com.holdbetter.fintechchatproject.navigation.profile.view

interface IUserViewer {
    fun setImage(resourceId: Int)
    fun setUserName(name: String)
    fun setStatus(isOnline: Boolean, statusText: String)

    fun startShimming()
    fun stopShimming()

    fun handleError(throwable: Throwable)
}