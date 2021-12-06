package com.holdbetter.fintechchatproject.room.services

import java.lang.Exception

class UnexpectedRoomException(val throwable: Throwable): Exception(throwable)