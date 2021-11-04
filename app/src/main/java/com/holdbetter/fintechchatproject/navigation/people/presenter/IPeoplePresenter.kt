package com.holdbetter.fintechchatproject.navigation.people.presenter

import com.holdbetter.fintechchatproject.model.StupidUser
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*

interface IPeoplePresenter {
    fun bind()
    fun unbind()
    fun getSortedUsersList(users: TreeSet<StupidUser>): Single<List<StupidUser>>
}