package com.holdbetter.fintechchatproject.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.holdbetter.fintechchatproject.room.entity.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

@Dao
interface PeopleDao {
    @Query("select * from users order by name")
    fun getUsers(): Single<List<UserEntity>>

    @Query("select * from users where id = :id limit 1")
    fun getUsersById(id: Long): Single<UserEntity>

    @Insert
    fun insertUsers(vararg users: UserEntity)

    @Query("delete from users")
    fun deleteUsers()

    fun applyUsers(users: List<UserEntity>): Completable {
        return Completable.create {
            applyUsersSynchronous(users)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    @Transaction
    fun applyUsersSynchronous(users: List<UserEntity>) {
        deleteUsers()
        insertUsers(*users.toTypedArray())
    }
}