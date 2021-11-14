package com.holdbetter.fintechchatproject.services

import androidx.fragment.app.Fragment
import com.holdbetter.fintechchatproject.main.ChatApplication

object FragmentExtensions {
    val Fragment.application
        get() = requireActivity().application as ChatApplication
}