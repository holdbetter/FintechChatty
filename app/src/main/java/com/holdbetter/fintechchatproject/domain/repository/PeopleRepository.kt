package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.PresenceResponse
import com.holdbetter.fintechchatproject.domain.repository.IRepository.Companion.TIMEOUT_MILLIS
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

    override fun getCachedUsers(ignorePresence: Boolean): Single<List<User>> {
        return peopleDao.getUsers()
            .map { it.toUserList(ignorePresence) }
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
            .flatMap { it.getUsers() }
            .map { it.members.toUserEntity() }
            .flatMap { addPresenceInfo(it) }
            .timeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .flatMapCompletable { cacheUsers(it) }
    }

    private fun addPresenceInfo(users: List<UserEntity>): Single<List<UserEntity>> {
        return api.sendStatus()
            .flatMap { applyPresence(it, users) }
    }

    private fun applyPresence(
        presenceResponse: PresenceResponse,
        users: List<UserEntity>
    ): Single<List<UserEntity>> {
        return Observable.fromIterable(presenceResponse.presences.entries)
            .map { entry ->
                users.getUserByMail(entry.key)
                    .also {
                        it.status = getStatusByThreshold(
                            presenceResponse.serverTimestamp,
                            entry.value.aggregated.timestamp
                        )
                    }
            }
            .toList()
    }

    private fun getStatusByThreshold(serverTimestamp: Double, userTimestamp: Long): String {
        val threshold = serverTimestamp - userTimestamp
        val offlineLimitSeconds = 400
        val idleLimitSeconds = 200
        return when {
            threshold > offlineLimitSeconds -> {
                TinkoffZulipApi.ZulipStatus.OFFLINE
            }
            threshold > idleLimitSeconds -> {
                TinkoffZulipApi.ZulipStatus.IDLE
            }
            else -> {
                TinkoffZulipApi.ZulipStatus.ACTIVE
            }
        }
    }

    private fun List<UserEntity>.getUserByMail(mail: String): UserEntity {
        return this.first { user -> user.mail == mail }
    }
}