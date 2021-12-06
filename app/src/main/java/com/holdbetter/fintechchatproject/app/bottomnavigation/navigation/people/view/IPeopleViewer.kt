package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import com.holdbetter.fintechchatproject.model.User

interface IPeopleViewer {
    fun setUsers(users: List<User>)
    fun shimming(turnOn: Boolean)
}
