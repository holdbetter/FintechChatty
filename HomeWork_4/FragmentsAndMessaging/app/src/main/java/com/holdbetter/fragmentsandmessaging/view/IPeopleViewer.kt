package com.holdbetter.fragmentsandmessaging.view

import com.holdbetter.fragmentsandmessaging.model.StupidUser

interface IPeopleViewer {
    fun setUsers(users: List<StupidUser>)
}
