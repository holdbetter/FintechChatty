package com.holdbetter.fragmentsandmessaging.presenter

import com.holdbetter.fragmentsandmessaging.model.HashtagStream
import com.holdbetter.fragmentsandmessaging.model.Topic

interface ITopicPresenter {
    fun bind()
    fun unbind()
    fun getTopicById(id: Int): Topic
    fun getStreamById(id: Int): HashtagStream
}
