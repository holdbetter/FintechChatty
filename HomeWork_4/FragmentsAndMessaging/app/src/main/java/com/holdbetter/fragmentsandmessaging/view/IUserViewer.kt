package com.holdbetter.fragmentsandmessaging.view

interface IUserViewer {
    fun setImage(resourceId: Int)
    fun setUserName(name: String)
    fun setStatus(isOnline: Boolean, statusText: String)
}