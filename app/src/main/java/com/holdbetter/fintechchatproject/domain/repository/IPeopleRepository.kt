package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.entity.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface IPeopleRepository : IRepository {
    var peopleHolder: List<User>?
    var lastSearchRequest: String?

    fun search(request: String)

    fun getUsersOnline(): Completable
    fun getCachedUsers(ignorePresence: Boolean = false): Single<List<User>>
    fun cacheUsers(users: List<UserEntity>): Completable

    fun startHandleSearchResults(): Observable<List<User>>
}