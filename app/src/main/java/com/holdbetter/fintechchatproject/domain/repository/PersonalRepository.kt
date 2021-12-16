package com.holdbetter.fintechchatproject.domain.repository

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.holdbetter.fintechchatproject.di.ApplicationScope
import com.holdbetter.fintechchatproject.domain.repository.IRepository.Companion.TIMEOUT_MILLIS
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ApplicationScope
class PersonalRepository @Inject constructor(
    private val personalDao: PersonalDao,
    private val app: Application,
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi
) : IPersonalRepository {
    private var _me: User? = null
    override val me: User
        get() = _me!!

    override val meId: Long
        get() = me.id

    override fun getMyselfOnline(): Completable {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { it.getMyself() }
            .map { it.toPersonalEntity() }
            .doOnSuccess { preloadPersonalAvatar(it.avatarUrl, app.applicationContext) }
            .timeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
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
            .doOnSuccess { me -> saveMeLocally(me) }
            .observeOn(Schedulers.io())
    }

    private fun saveMeLocally(me: User) {
        if (_me == null) {
            this._me = me
        }
    }

    override fun cacheMyself(me: PersonalEntity): Completable {
        return personalDao.applyMyself(me)
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(Completable.create {
                saveMeLocally(me.toUser())
                it.onComplete()
            })
            .observeOn(Schedulers.io())
    }
}