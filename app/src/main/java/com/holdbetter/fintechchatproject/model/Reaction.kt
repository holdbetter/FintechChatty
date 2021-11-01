package com.holdbetter.fintechchatproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reaction(val usersId: ArrayList<Int>, val emojiCode: String) : Parcelable