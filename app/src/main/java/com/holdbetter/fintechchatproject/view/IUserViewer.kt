package com.holdbetter.fintechchatproject.view

interface IUserViewer {
    fun setImage(resourceId: Int)
    fun setUserName(name: String)
    fun setStatus(isOnline: Boolean, statusText: String)
}