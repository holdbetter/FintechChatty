package com.holdbetter.fintechchatproject.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.holdbetter.fintechchatproject.room.dao.EmojiDao
import com.holdbetter.fintechchatproject.room.dao.PeopleDao
import com.holdbetter.fintechchatproject.room.dao.PersonalDao
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.room.entity.*

@Database(
    entities = [
        StreamEntity::class,
        TopicEntity::class,
        EmojiEntity::class,
        ApiEmojiEntity::class,
        UserEntity::class,
        PersonalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun streamDao(): StreamDao
    abstract fun emojiDao(): EmojiDao
    abstract fun peopleDao(): PeopleDao
    abstract fun personalDao(): PersonalDao

    companion object {
        const val STREAM_TABLE_NAME = "streams"
        const val TOPIC_TABLE_NAME = "topics"
        const val EMOJI_TABLE_NAME = "emoji"
        const val API_EMOJI_TABLE_NAME = "api_emoji"
        const val USERS_TABLE_NAME = "users"
        const val PERSONAL_TABLE_NAME = "personal"

        @Volatile
        private var instance: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_project_db"
                ).build()

                instance = db
                db
            }
        }
    }
}