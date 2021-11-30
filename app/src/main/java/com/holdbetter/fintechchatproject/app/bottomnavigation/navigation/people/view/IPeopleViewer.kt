package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view

import com.holdbetter.fintechchatproject.model.User

interface IPeopleViewer {
    fun setUsers(users: List<User>)

    fun startShimmer()
    fun stopShimmer()
    fun bind()
    fun unbind()
}
