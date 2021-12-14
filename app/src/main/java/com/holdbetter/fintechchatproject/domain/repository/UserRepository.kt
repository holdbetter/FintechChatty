package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.dao.PeopleDao
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toUser
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val peopleDao: PeopleDao
) : IUserRepository {
    override fun getUserById(userId: Long): Single<User> {
        return peopleDao.getUsersById(userId)
            .subscribeOn(Schedulers.io())
            .map { it.toUser() }
    }
}