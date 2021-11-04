package com.holdbetter.fintechchatproject.model.repository

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.StupidUser
import com.holdbetter.fintechchatproject.model.Topic
import java.util.*

interface IChatRepository {
    val users: TreeSet<StupidUser>
    val currentUser: StupidUser
    val testingMessages: MutableList<Message>
    val bruhMessages: MutableList<Message>
    val topics: MutableList<Topic>
    val hashtagStreams: MutableList<HashtagStream>

    object UserId {
        const val VILEN_ID = 0
        const val ALEXEY_ID = 1
        const val ASAP_ROCKY = 2
    }

    object TopicId {
        const val TESTING_ID = 0
        const val BRUH_ID = 1
    }
}