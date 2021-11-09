package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.model.Message
import io.reactivex.rxjava3.core.Single

interface IChatRepository {
    fun getMessages(narrow: Narrow): Single<List<Message>>
}