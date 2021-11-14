package com.holdbetter.fintechchatproject.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity

@Database(entities = [HashtagStreamEntity::class], version = 1, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun streamDao(): StreamDao

    companion object {
        const val STREAM_TABLE_NAME = "streams"

        @Volatile
        private var instance: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_project_db").build()

                instance = db
                db
            }
        }
    }
}