package com.holdbetter.chatonrecycler.services

import com.holdbetter.chatonrecycler.components.ReactionView
import kotlin.reflect.KProperty

class RequestLayoutNotNullCount {
    private var reactionCount: Int = 0

    operator fun getValue(thisRef: ReactionView, property: KProperty<*>): Int {
        return reactionCount
    }

    operator fun setValue(thisRef: ReactionView, property: KProperty<*>, value: Int) {
        reactionCount = value
        thisRef.requestLayout()
    }
}