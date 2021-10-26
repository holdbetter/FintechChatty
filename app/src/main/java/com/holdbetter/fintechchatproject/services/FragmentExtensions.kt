package com.holdbetter.fintechchatproject.services

import androidx.fragment.app.Fragment
import com.holdbetter.fintechchatproject.ChatApplication

object FragmentExtensions {
    val Fragment.chatRepository get() = (this.activity?.application as ChatApplication).repository
}