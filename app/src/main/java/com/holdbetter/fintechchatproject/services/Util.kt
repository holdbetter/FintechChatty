package com.holdbetter.fintechchatproject.services

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import java.util.*

object Util { // неопределенные, фантомные функции, которые может пригодятся

    fun getEmojiByCode(code: Int): String {
        return Character.toChars(code).concatToString()
    }

    fun getEmojiByCode(code: String): String {
        return Character.toChars(code.substring(2).toInt(16)).concatToString()
    }

    val supportedEmojiList = listOf(
        (0x1F601..0x1F64F).toList(),
        (0x2702..0x27B0).toList(),
        (0x1F680..0x1F6C0).toList(),
        (0x1F170..0x1F251).toList()
    ).flatten()

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