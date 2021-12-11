package com.holdbetter.fintechchatproject.app.bottomnavigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

interface INavigationAssistant {
    val cachedPages: MutableMap<Int, Fragment>
    val pagesToBeAdded: Map<Int, Fragment>
    val currentFragmentPair: Pair<Int, Fragment>?

    fun navigateTo(@IdRes fragmentMenuId: Int)
}
