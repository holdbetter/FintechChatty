package com.holdbetter.fragmentsandmessaging.services

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.TypedValue
import androidx.fragment.app.Fragment
import com.holdbetter.fragmentsandmessaging.ChatApplication
import java.util.*

object Util { // aka pomoika, но уже поменьше
    val calendarInstance: Calendar = Calendar.getInstance()
    var currentUserId = 0

    fun getEmojiByCode(code: Int): String {
        return Character.toChars(code).concatToString()
    }

    fun getEmojiByCode(code: String): String {
        return Character.toChars(code.substring(2).toInt(16)).concatToString()
    }

    val supportedEmojiList = (0x1F601..0x1F64F).toList()

    // source: https://proandroiddev.com/complex-ui-animation-on-android-8f7a46f4aec4
    inline fun getValueAnimator(
        forward: Boolean = true, duration: Long, interpolator: TimeInterpolator,
        crossinline updateListener: (progress: Float) -> Unit,
    ): ValueAnimator {
        val valueAnimator = if (forward) {
            ValueAnimator.ofFloat(0f, 1f)
        } else {
            ValueAnimator.ofFloat(1f, 0f)
        }

        valueAnimator.addUpdateListener { updateListener(it.animatedValue as Float) }
        valueAnimator.duration = duration
        valueAnimator.interpolator = interpolator
        return valueAnimator
    }
}