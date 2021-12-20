package com.holdbetter.fintechchatproject.services

object Util {
    fun getEmojiByCode(code: Int): String {
        return Character.toChars(code).concatToString()
    }
}