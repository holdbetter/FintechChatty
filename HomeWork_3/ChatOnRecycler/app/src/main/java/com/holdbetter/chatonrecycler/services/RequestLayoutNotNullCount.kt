package com.holdbetter.chatonrecycler.services

import com.holdbetter.chatonrecycler.components.ReactionView
import kotlin.reflect.KProperty

class RequestLayoutNotNullCount {
    private var reactionCount: Int = -1

    operator fun getValue(thisRef: ReactionView, property: KProperty<*>): Int {
        return if (reactionCount != -1) reactionCount else 0
    }

    operator fun setValue(thisRef: ReactionView, property: KProperty<*>, value: Int) {
        if (reactionCount != -1) {
            reactionCount = value
            thisRef.requestLayout()
            return
        }
        reactionCount = value
    }
}