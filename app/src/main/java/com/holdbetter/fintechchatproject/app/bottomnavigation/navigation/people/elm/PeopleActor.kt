package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import com.holdbetter.fintechchatproject.domain.repository.IPeopleRepository
import com.holdbetter.fintechchatproject.room.services.UnexpectedRoomException
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class PeopleActor @Inject constructor(private val peopleRepository: IPeopleRepository) :
    Actor<PeopleCommand, PeopleEvent> {
    override fun execute(command: PeopleCommand): Observable<PeopleEvent> {
        return when (command) {
            PeopleCommand.GetCachedPeople -> peopleRepository.getCachedUsers().mapEvents(
                successEventMapper = { users -> PeopleEvent.Internal.DataLoaded(users) },
                failureEventMapper = { error ->
                    PeopleEvent.Internal.DbDataError(
                        UnexpectedRoomException(error)
                    )
                }
            )
            PeopleCommand.GetCachedPeopleWithoutPresence -> peopleRepository.getCachedUsers(ignorePresence = true).mapEvents(
                successEventMapper = { users -> PeopleEvent.Internal.DataLoaded(users) },
                failureEventMapper = { error ->
                    PeopleEvent.Internal.DbDataError(
                        UnexpectedRoomException(error)
                    )
                }
            )
            PeopleCommand.LoadPeople -> peopleRepository.getUsersOnline().mapEvents(
                successEvent = PeopleEvent.Internal.DataReady,
                failureEventMapper = { error -> PeopleEvent.Internal.OnlineDataError(error) }
            )
            PeopleCommand.ObserveSearching -> peopleRepository.startHandleSearchResults()
                .mapSuccessEvent {
                    PeopleEvent.Internal.Searched(it)
                }
            is PeopleCommand.RunSearch -> {
                peopleRepository.search(command.request)
                Observable.empty()
            }
        }
    }
}