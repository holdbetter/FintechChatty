package com.holdbetter.fintechchatproject.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.holdbetter.fintechchatproject.room.entity.PersonalEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers

@Dao
interface PersonalDao {
    @Query("select * from personal limit 1")
    fun getMyself(): Maybe<PersonalEntity>

    @Query("delete from personal")
    fun deleteMyself()

    @Insert
    fun insertMyself(me: PersonalEntity)

    @Transaction
    fun applySynchronously(me: PersonalEntity) {
        deleteMyself()
        insertMyself(me)
    }

    fun applyMyself(me: PersonalEntity): Completable {
        return Completable.create {
            applySynchronously(me)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }
}