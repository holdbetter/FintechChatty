package com.holdbetter.fintechchatproject.navigation.people.viewmodel

import androidx.lifecycle.ViewModel
import com.holdbetter.fintechchatproject.domain.repository.IPeopleRepository
import com.holdbetter.fintechchatproject.domain.repository.PeopleRepository
import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class PeopleViewModel : ViewModel() {
    private val peopleRepository: IPeopleRepository = PeopleRepository()
    private var cachedUsers: List<User>? = null

    // пока не хочу через LiveData везде ходить
    fun getUsers(): Single<List<User>> {
        return Observable.concat(
            getCachedUsers(),
            getUsersOnline()
        ).subscribeOn(Schedulers.io())
            .firstElement()
            .delay(1000, TimeUnit.MILLISECONDS)
            .map { users -> users.sortedBy { it.name } }
            .toSingle()
    }

    fun getUsersById(userId: Long): Single<User> {
        return getUsers()
            .subscribeOn(Schedulers.io())
            .map { users -> users.find { it.id == userId }!! }
    }

    private fun getCachedUsers(): Observable<List<User>> {
        return Observable.create<List<User>> { emitter ->
            cachedUsers?.let {
                emitter.onNext(it)
            }
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    private fun getUsersOnline(): Observable<List<User>> {
        return peopleRepository.getUsers()
            .doOnSuccess { cachedUsers = it }
            .toObservable()
    }
}