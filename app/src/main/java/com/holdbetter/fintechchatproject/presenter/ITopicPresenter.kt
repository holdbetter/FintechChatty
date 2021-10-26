package com.holdbetter.fintechchatproject.presenter

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic

interface ITopicPresenter {
    fun bind()
    fun unbind()
    fun getTopicById(id: Int): Topic
    fun getStreamById(id: Int): HashtagStream
}
