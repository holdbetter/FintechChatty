package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.view

import com.holdbetter.fintechchatproject.model.User

interface IUserViewer {
    fun bindUser(user: User?)

    fun shimming(turnOn: Boolean)
    fun handleError(error: Throwable)
}