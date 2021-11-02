package com.holdbetter.fintechchatproject.services

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import java.util.concurrent.TimeUnit

object RxExtensions {
    fun <T> Observable<T>.delayEach(interval: Long, timeUnit: TimeUnit): Observable<T> =
        Observable.zip(
            this,
            Observable.interval(interval, timeUnit),
            { item, _ -> item }
        )
}