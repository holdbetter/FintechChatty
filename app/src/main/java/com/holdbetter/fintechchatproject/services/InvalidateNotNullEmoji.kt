package com.holdbetter.fintechchatproject.services

import com.holdbetter.fintechchatproject.ui.ReactionView
import kotlin.reflect.KProperty

class InvalidateNotNullEmoji {
    private var emojiUnicode: String = ""

    operator fun getValue(thisRef: ReactionView, property: KProperty<*>): String {
        return emojiUnicode
    }

    operator fun setValue(thisRef: ReactionView, property: KProperty<*>, value: String) {
        if (value.isNotEmpty()) {
            emojiUnicode = Util.getEmojiByCode(value.uppercase().toInt(16))
        }
    }
}