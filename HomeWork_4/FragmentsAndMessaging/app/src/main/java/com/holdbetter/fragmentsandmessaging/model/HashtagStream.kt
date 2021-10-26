package com.holdbetter.fragmentsandmessaging.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HashtagStream(val id: Int, val name: String, val topics: ArrayList<Topic>) : Parcelable