package com.holdbetter.fintechchatproject.services

import android.animation.TimeInterpolator
import android.animation.ValueAnimator

object Util {
    fun getEmojiByCode(code: Int): String {
        return Character.toChars(code).concatToString()
    }
}