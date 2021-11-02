package com.holdbetter.fintechchatproject.kttests

import android.util.Log
import io.reactivex.rxjava3.kotlin.toObservable
import java.util.concurrent.TimeUnit

fun main() {
    arrayOf("Vilen", "Leysan").toObservable()
        .delay(5000, TimeUnit.MILLISECONDS)
        .subscribe { println(it) }

    readLine()
}