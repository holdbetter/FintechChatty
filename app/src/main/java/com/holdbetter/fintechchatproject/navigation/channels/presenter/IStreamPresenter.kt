package com.holdbetter.fintechchatproject.navigation.channels.presenter

interface IStreamPresenter {
    fun bind()
    fun unbind()

    fun startSearch(input: String)
}
