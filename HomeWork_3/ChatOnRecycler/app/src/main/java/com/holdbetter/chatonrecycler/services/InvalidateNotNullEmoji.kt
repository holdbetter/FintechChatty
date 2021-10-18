package com.holdbetter.chatonrecycler.services

import com.holdbetter.chatonrecycler.components.ReactionView
import kotlin.reflect.KProperty

class InvalidateNotNullEmoji {
    private var emojiUnicode: String = ""

    operator fun getValue(thisRef: ReactionView, property: KProperty<*>): String {
        return if (emojiUnicode.isNotEmpty()) emojiUnicode else getRandomUnicode()
    }

    operator fun setValue(thisRef: ReactionView, property: KProperty<*>, value: String) {
        if (emojiUnicode.isNotEmpty()) {
            emojiUnicode = value
            thisRef.invalidate()
            return
        }

        emojiUnicode = value
    }

    private fun getRandomUnicode(): String {
        Util.emojiUnicode.shuffle()
        return Util.emojiUnicode[0]
    }
}