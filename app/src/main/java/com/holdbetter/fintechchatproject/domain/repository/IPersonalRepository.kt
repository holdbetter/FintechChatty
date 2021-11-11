package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.core.Single

interface IPersonalRepository {
    fun getMyself(): Single<User>
}