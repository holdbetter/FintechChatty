package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toUser
import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class PeopleRepository : IPeopleRepository {
    override fun getUsers(): Single<List<User>> {
        return ServiceProvider.api
            .getUsers()
            .subscribeOn(Schedulers.io())
            .map { it.members.map { member -> member.toUser() } }
    }
}