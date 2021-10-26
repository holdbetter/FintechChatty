package com.holdbetter.fragmentsandmessaging.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Topic(val id: Int, val name: String, val color: String, val messages: ArrayList<Message>, val hashtagId: Int) :
    Parcelable