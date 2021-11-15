package com.holdbetter.fintechchatproject.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.room.dao.TopicDao
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import com.holdbetter.fintechchatproject.room.entity.TopicEntity

@Database(entities = [HashtagStreamEntity::class, TopicEntity::class], version = 1, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun streamDao(): StreamDao
    abstract fun topicDao(): TopicDao

    companion object {
        const val STREAM_TABLE_NAME = "streams"
        const val TOPIC_TABLE_NAME = "topics"

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