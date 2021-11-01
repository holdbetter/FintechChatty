package com.holdbetter.fintechchatproject.navigation.people.presenter

import com.holdbetter.fintechchatproject.model.StupidUser
import java.util.*

interface IPeoplePresenter {
    fun bind()
    fun unbind()
    fun getSortedUsersList(users: TreeSet<StupidUser>): List<StupidUser>
}