package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.holdbetter.fintechchatproject.domain.repository.IPeopleRepository
import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PeopleViewModel(private val peopleRepository: IPeopleRepository) : ViewModel() {
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

class PeopleViewModelFactory @Inject constructor(
    private val peopleRepository: IPeopleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeopleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PeopleViewModel(peopleRepository) as T
        }
        throw IllegalArgumentException("Wrong ViewModel class")
    }
}