package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.entity.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface IPeopleRepository: IRepository {
    fun getUsersOnline(): Completable
    fun getCachedUsers(): Single<List<User>>
    fun getUsersById(userId: Long): Single<User>
    fun cacheUsers(users: List<UserEntity>): Completable
}