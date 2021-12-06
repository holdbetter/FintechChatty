package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import com.holdbetter.fintechchatproject.domain.repository.IPeopleRepository
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class DetailUserActor @Inject constructor(private val peopleRepository: IPeopleRepository) : Actor<DetailUserCommand, DetailUserEvent> {
    override fun execute(command: DetailUserCommand): Observable<DetailUserEvent> {
        return when(command) {
            is DetailUserCommand.LoadUser -> peopleRepository.getUsersById(command.userId).mapEvents(
                successEventMapper = { user -> DetailUserEvent.Internal.DataLoaded(user) },
                failureEventMapper = { throwable -> DetailUserEvent.Internal.DataError(throwable) }
            )
        }
    }
}