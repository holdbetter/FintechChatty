package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toUserEntity
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.dao.PeopleDao
import com.holdbetter.fintechchatproject.room.entity.UserEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toUserList
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val peopleDao: PeopleDao,
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi
) : IPeopleRepository {
    override var peopleHolder: List<User>? = null
    override var lastSearchRequest: String? = null

    private val searchRequest: PublishSubject<String> = PublishSubject.create()

    override fun getCachedUsers(): Single<List<User>> {
        return peopleDao.getUsers()
            .map { it.toUserList() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { saveLocally(it) }
            .observeOn(Schedulers.io())
    }

    override fun startHandleSearchResults(): Observable<List<User>> {
        return searchRequest.subscribeOn(Schedulers.io())
            .debounce(150, TimeUnit.MILLISECONDS)
            .switchMapSingle(::getSearchResponse)
    }

    private fun getSearchResponse(request: String): Single<List<User>> {
        return if (request.isBlank()) {
            Single.just(peopleHolder!!)
        } else {
            peopleHolder!!.toObservable()
                .subscribeOn(Schedulers.computation())
                .filter { isMatchingPattern(request, it.name) }
                .toList()
        }
    }

    private fun isMatchingPattern(searchInput: String, userNameToCheck: String): Boolean {
        val regex = Regex("^$searchInput", RegexOption.IGNORE_CASE)
        val usersNameParts = userNameToCheck.split(" ")

        var contains = false
        for (namePart in usersNameParts) {
            contains = regex.containsMatchIn(namePart) || contains
        }
        return contains
    }

    override fun search(request: String) {
        lastSearchRequest = request
        searchRequest.onNext(request)
    }

    private fun saveLocally(users: List<User>) {
        peopleHolder = users
    }

    override fun cacheUsers(users: List<UserEntity>): Completable {
        return peopleDao.applyUsers(users)
    }

    override fun getUsersOnline(): Completable {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .delay(2000, TimeUnit.MILLISECONDS)
            .flatMap { it.getUsers() }
            .map { it.members.map { member -> member.toUserEntity() } }
            .flatMapCompletable { cacheUsers(it) }
    }
}