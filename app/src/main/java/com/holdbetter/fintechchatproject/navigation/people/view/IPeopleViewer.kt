package com.holdbetter.fintechchatproject.navigation.people.view

import com.holdbetter.fintechchatproject.model.StupidUser

interface IPeopleViewer {
    fun setUsers(users: List<StupidUser>)

    fun startShimmer()
    fun stopShimmer()
}
