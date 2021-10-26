package com.holdbetter.fintechchatproject.presenter

import com.holdbetter.fintechchatproject.model.IChatRepository
import com.holdbetter.fintechchatproject.model.StupidUser
import com.holdbetter.fintechchatproject.view.IPeopleViewer
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