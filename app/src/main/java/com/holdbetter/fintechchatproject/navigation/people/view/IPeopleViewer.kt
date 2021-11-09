package com.holdbetter.fintechchatproject.navigation.people.view

import com.holdbetter.fintechchatproject.model.User

interface IPeopleViewer {
    fun setUsers(users: List<User>)

    fun startShimmer()
    fun stopShimmer()
}
