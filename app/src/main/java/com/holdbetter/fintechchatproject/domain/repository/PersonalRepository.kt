package com.holdbetter.fintechchatproject.domain.repository

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.holdbetter.fintechchatproject.di.ApplicationScope
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toPersonalEntity
import com.holdbetter.fintechchatproject.model.User
import com.holdbetter.fintechchatproject.room.dao.PersonalDao
import com.holdbetter.fintechchatproject.room.entity.PersonalEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toUser
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@ApplicationScope
class PersonalRepository @Inject constructor(
    private val personalDao: PersonalDao,
    private val app: Application,
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi
) : IPersonalRepository {
    private var _currentUserId: Long? = null

    override val currentUserId: Long
        get() = _currentUserId!!

    override fun getMyselfOnline(): Completable {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { it.getMyself() }
            .map { it.toPersonalEntity() }
            .doOnSuccess { preloadPersonalAvatar(it.avatarUrl, app.applicationContext) }
            .flatMapCompletable { cacheMyself(it) }
    }

    private fun preloadPersonalAvatar(avatarUrl: String, applicationContext: Context): Drawable {
        return Glide.with(applicationContext)
            .load(avatarUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .submit()
            .get()
    }

    override fun getCachedMyself(): Maybe<User> {
        return personalDao.getMyself()
            .subscribeOn(Schedulers.io())
            .map { it.toUser() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { saveIdLocally(it.id) }
            .observeOn(Schedulers.io())
    }

    private fun saveIdLocally(userId: Long) {
        if (_currentUserId == null) {
            _currentUserId = userId
        }
    }

    override fun cacheMyself(me: PersonalEntity): Completable {
        return personalDao.applyMyself(me)
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(Completable.create {
                saveIdLocally(me.id)
                it.onComplete()
            })
            .observeOn(Schedulers.io())
    }
}