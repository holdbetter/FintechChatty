package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PersonalViewModel(private val personalRepository: IPersonalRepository) : ViewModel() {
    private var _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User>
        get() = _currentUser

    val currentUserId: Long
        get() = if (currentUser.value != null) {
            currentUser.value!!.id
        } else {
            -1
        }

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
        return Observable.create<User> { emitter ->
            _currentUser.value?.let {
                emitter.onNext(it)
            }
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
            .delay(250, TimeUnit.MILLISECONDS)
    }
}

class PersonalViewModelFactory @Inject constructor(
    private val personalRepository: IPersonalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonalViewModel(personalRepository) as T
        }
        throw IllegalArgumentException("Wrong ViewModel class")
    }
}