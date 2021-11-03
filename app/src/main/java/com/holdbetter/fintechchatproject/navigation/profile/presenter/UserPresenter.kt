package com.holdbetter.fintechchatproject.navigation.profile.presenter

import com.holdbetter.fintechchatproject.model.StupidUser
import com.holdbetter.fintechchatproject.model.repository.IChatRepository
import com.holdbetter.fintechchatproject.navigation.profile.view.IUserViewer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class UserPresenter(
    private val userId: Int,
    private val chatRepository: IChatRepository,
    private val userViewer: IUserViewer,
) : IUserPresenter {
    private val compositeDisposable = CompositeDisposable()

    private val idsForRandom = arrayOf(userId, -1)

    override fun getUserById(userId: Int): Single<StupidUser> {
        userViewer.startShimming()
        val id = getRandomId()
        return Single.fromCallable { chatRepository.users.find { it.id == id }!! }
            .delay(3000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .doOnError { Thread.sleep(1000) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { userViewer.stopShimming() }
    }

    override fun getStatus(isOnline: Boolean) = if (isOnline) "online" else "offline"

    override fun bind() {
        getUserById(userId)
            .subscribeBy(
                onSuccess = ::setUser,
                onError = userViewer::handleError
            ).addTo(compositeDisposable)
    }

    override fun unbind() {
        compositeDisposable.clear()
    }

    private fun setUser(user: StupidUser) {
        userViewer.setImage(user.avatarResourceId)
        userViewer.setUserName(user.name)
        userViewer.setStatus(user.isOnline, getStatus(user.isOnline))
    }

    private fun getRandomId(): Int {
        val id = Random.nextInt(0, 2)
        return idsForRandom[id]
    }
}