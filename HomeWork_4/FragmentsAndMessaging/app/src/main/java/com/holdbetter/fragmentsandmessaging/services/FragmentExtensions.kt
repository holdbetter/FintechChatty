package com.holdbetter.fragmentsandmessaging.services

import androidx.fragment.app.Fragment
import com.holdbetter.fragmentsandmessaging.ChatApplication

object FragmentExtensions {
    val Fragment.chatRepository get() = (this.activity?.application as ChatApplication).repository
}