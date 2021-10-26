package com.holdbetter.fragmentsandmessaging.presenter

import com.holdbetter.fragmentsandmessaging.model.IChatRepository
import com.holdbetter.fragmentsandmessaging.model.StupidUser
import com.holdbetter.fragmentsandmessaging.view.IPeopleViewer
import java.util.*

class PeoplePresenter(
    private val chatRepository: IChatRepository,
    private val peopleFragment: IPeopleViewer,
) : IPeoplePresenter {
    override fun getSortedUsersList(users: TreeSet<StupidUser>): List<StupidUser> {
        return users.sortedWith(compareBy({ !it.isOnline }, { it.name }))
    }

    override fun bind() {
        val users = getSortedUsersList(chatRepository.users)
        peopleFragment.setUsers(users)
    }

    override fun unbind() {

    }
}