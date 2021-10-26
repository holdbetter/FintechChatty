package com.holdbetter.fragmentsandmessaging.services

import android.content.Context
import android.util.TypedValue

object ContextExtesions {
    fun Context.dpToPx(dp: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        )
        return px.toInt()
    }

    fun Context.spToPx(sp: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            this.resources.displayMetrics
        )
        return px.toInt()
    }
}