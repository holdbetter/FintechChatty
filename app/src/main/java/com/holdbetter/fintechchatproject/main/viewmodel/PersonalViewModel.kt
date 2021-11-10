package com.holdbetter.fintechchatproject.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.holdbetter.fintechchatproject.main.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.main.repository.PersonalRepository
import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class PersonalViewModel : ViewModel() {
    private var _currentUser: MutableLiveData<User> = MutableLiveData()
    private val currentUser: LiveData<User>
        get() = _currentUser

    val currentUserId: Long
        get() = if (currentUser.value != null) {
            currentUser.value!!.id
        } else {
            -1
        }

    private val personalRepository: IPersonalRepository = PersonalRepository()

    fun getMyself(): Maybe<User> {
        return Observable.concat(getCachedMyself(), personalRepository.getMyself().toObservable())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (_currentUser.value != it) {
                    _currentUser.value = it
                }
            }
            .firstElement()
    }

    private fun getCachedMyself(): Observable<User> {
        return Observable.create { emitter ->
            _currentUser.value?.let {
                emitter.onNext(it)
            }
            emitter.onComplete()
        }
    }
}