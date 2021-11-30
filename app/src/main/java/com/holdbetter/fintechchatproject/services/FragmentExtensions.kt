package com.holdbetter.fintechchatproject.services

import androidx.fragment.app.Fragment
import com.holdbetter.fintechchatproject.services.ContextExtensions.app

object FragmentExtensions {
    val Fragment.app
        get() = requireActivity().app
}