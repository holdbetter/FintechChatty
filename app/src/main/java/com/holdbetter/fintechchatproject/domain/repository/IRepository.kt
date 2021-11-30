package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import io.reactivex.rxjava3.core.Single

interface IRepository {
    val api: TinkoffZulipApi

    fun getApi(isConnected: Boolean): Single<TinkoffZulipApi> {
        return Single.create { emitter ->
            if (isConnected) {
                emitter.onSuccess(api)
            } else {
                emitter.onError(NotConnectedException())
            }
        }
    }
}