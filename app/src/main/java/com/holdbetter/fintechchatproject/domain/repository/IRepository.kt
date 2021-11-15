package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import io.reactivex.rxjava3.core.Single

interface IRepository {
    fun getApi(isConnected: Boolean): Single<TinkoffZulipApi> {
        return Single.create { emitter ->
            if (isConnected) {
                emitter.onSuccess(ServiceProvider.api)
            } else {
                emitter.onError(NotConnectedException())
            }
        }
    }
}