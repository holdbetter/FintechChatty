package com.holdbetter.fintechchatproject.model

import java.util.*
import kotlin.collections.ArrayList

interface IChatRepository {
    val users: TreeSet<StupidUser>
    val currentUser: StupidUser
    val testingMessages: ArrayList<Message>
    val bruhMessages: ArrayList<Message>
    val topics: ArrayList<Topic>
    val hashtagStreams: ArrayList<HashtagStream>

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