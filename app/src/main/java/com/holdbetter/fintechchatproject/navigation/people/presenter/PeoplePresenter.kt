package com.holdbetter.fintechchatproject.navigation.people.presenter

import com.holdbetter.fintechchatproject.domain.repository.IChatRepository
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.navigation.people.view.IPeopleViewer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class PeoplePresenter(
    private val chatRepository: IChatRepository? = null,
    private val peopleFragment: IPeopleViewer,
) : IPeoplePresenter {
    private val compositeDisposable = CompositeDisposable()

    override fun getSortedUsersList(users: TreeSet<User>): Single<List<User>> {
        peopleFragment.startShimmer()
        return Single.just(users)
            .subscribeOn(Schedulers.io())
            .delay(2500, TimeUnit.MILLISECONDS)
            .map { users.sortedWith(compareBy({ !it.isOnline }, { it.name })) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { peopleFragment.stopShimmer() }
    }

    override fun bind() {
//        getSortedUsersList(chatRepository.users)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy(
//                onSuccess = peopleFragment::setUsers
//            ).addTo(compositeDisposable)
    }

    override fun unbind() {
        compositeDisposable.clear()
    }
}