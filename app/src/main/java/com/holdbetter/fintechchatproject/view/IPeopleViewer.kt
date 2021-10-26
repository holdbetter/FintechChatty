package com.holdbetter.fintechchatproject.view

import com.holdbetter.fintechchatproject.model.StupidUser

interface IPeopleViewer {
    fun setUsers(users: List<StupidUser>)
}
