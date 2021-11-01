package com.holdbetter.fintechchatproject.services

import androidx.core.text.isDigitsOnly
import com.holdbetter.fintechchatproject.ui.ReactionView
import kotlin.reflect.KProperty

class InvalidateNotNullEmoji {
    private var emojiUnicode: String = ""

    operator fun getValue(thisRef: ReactionView, property: KProperty<*>): String {
        return emojiUnicode
    }

    operator fun setValue(thisRef: ReactionView, property: KProperty<*>, value: String) {
        if (value.isNotEmpty() && value.isDigitsOnly()) {
            if (value.toInt() in Util.supportedEmojiList) {
                emojiUnicode = Util.getEmojiByCode(value.toInt())
            } else {
                throw NotSupportedSymbol("This symbol isn't emoji or it's not in supported list of emoji")
            }
        } else if (value.isNotEmpty()) {
            val bigUNotation = value.substring(0, 2)
            val hexPart = value.substring(2).toInt(16)

            if (bigUNotation == "U+" && hexPart in Util.supportedEmojiList) {
                emojiUnicode = Util.getEmojiByCode(hexPart)
            } else {
                throw NotSupportedSymbol("This symbol isn't emoji or it's not in supported list of emoji")
            }
        }
    }
}