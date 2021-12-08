package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.elm

import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.room.services.UnexpectedRoomException
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class ProfileActor @Inject constructor(private val personalRepository: IPersonalRepository) :
    Actor<ProfileCommand, ProfileEvent> {
    override fun execute(command: ProfileCommand): Observable<ProfileEvent> {
        return when (command) {
            ProfileCommand.GetCachedMyself -> personalRepository.getCachedMyself().mapEvents(
                successEventMapper = { user -> ProfileEvent.Internal.DataLoaded(user) },
                completionEvent = ProfileEvent.Internal.DbEmpty,
                failureEventMapper = { error ->
                    ProfileEvent.Internal.DbDataError(
                        UnexpectedRoomException(error)
                    )
                }
            )
            ProfileCommand.LoadMyself -> personalRepository.getMyselfOnline().mapEvents(
                successEvent = ProfileEvent.Internal.DataReady,
                failureEventMapper = { error -> ProfileEvent.Internal.OnlineDataError(error) }
            )
        }
    }
}