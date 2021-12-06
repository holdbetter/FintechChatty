package com.holdbetter.fintechchatproject.services

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.services.ContextExtensions.app

object FragmentExtensions {
    val Fragment.app
        get() = requireActivity().app

    fun Fragment.createStyledSnackbar(
        @StringRes textId: Int? = null,
        @StringRes actionTextId: Int? = null,
        action: () -> Unit = {},
        length: Int = Snackbar.LENGTH_LONG,
    ): Snackbar {
        val appResources = resources
        val appTheme = requireActivity().theme

        return Snackbar.make(
            requireView(),
            getString(textId ?: R.string.default_error),
            length
        ).apply {
            setActionTextColor(appResources.getColor(R.color.blue_and_green, appTheme))
            setTextColor(appResources.getColor(android.R.color.black, appTheme))
            setBackgroundTint(appResources.getColor(R.color.white, appTheme))

            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                .apply {
                    typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
                }

            actionTextId?.let {
                setAction(actionTextId) {
                    action()
                }
            }
        }
    }
}