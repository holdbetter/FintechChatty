package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import com.holdbetter.fintechchatproject.model.User

interface IDetailUserViewer {
    fun bindUser(user: User?)

    fun shimming(turnOn: Boolean)
    fun handleError()
}