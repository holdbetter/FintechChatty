package com.holdbetter.fragmentsandmessaging.presenter

import com.holdbetter.fragmentsandmessaging.model.StupidUser
import java.util.*

interface IPeoplePresenter {
    fun bind()
    fun unbind()
    fun getSortedUsersList(users: TreeSet<StupidUser>): List<StupidUser>
}