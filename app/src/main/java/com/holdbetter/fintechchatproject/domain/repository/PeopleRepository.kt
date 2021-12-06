package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toUser
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toUserEntity
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.dao.PeopleDao
import com.holdbetter.fintechchatproject.room.entity.UserEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toUser
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toUserList
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val peopleDao: PeopleDao,
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi
) : IPeopleRepository {

    override fun getUsersById(userId: Long): Single<User> {
        return peopleDao.getUsersById(userId)
            .map { it.toUser() }
    }

    override fun getCachedUsers(): Single<List<User>> {
        return peopleDao.getUsers()
            .map { it.toUserList() }
    }

    override fun cacheUsers(users: List<UserEntity>): Completable {
        return peopleDao.applyUsers(users)
    }

    override fun getUsersOnline(): Completable {
        return connectivityManager.isConnected
            .flatMap { getApi(it) }
            .flatMap { it.getUsers() }
            .map { it.members.map { member -> member.toUserEntity() } }
            .flatMapCompletable { cacheUsers(it) }
    }
}