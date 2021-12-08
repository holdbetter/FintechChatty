package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.entity.PersonalEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

interface IPersonalRepository : IRepository {
    val currentUserId: Long

    fun getMyselfOnline(): Completable
    fun getCachedMyself(): Maybe<User>

    fun cacheMyself(me: PersonalEntity): Completable
}