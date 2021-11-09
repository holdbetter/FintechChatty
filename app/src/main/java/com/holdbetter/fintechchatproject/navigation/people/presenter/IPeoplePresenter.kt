package com.holdbetter.fintechchatproject.navigation.people.presenter

import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.core.Single
import java.util.*

interface IPeoplePresenter {
    fun bind()
    fun unbind()
    fun getSortedUsersList(users: TreeSet<User>): Single<List<User>>
}