package com.real.vibechat.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.real.vibechat.data.room.ratelimit.MessageLimitDao
import com.real.vibechat.data.room.ratelimit.MessageLimitEntity
import com.real.vibechat.data.room.ratelimit.OtpLimitDao
import com.real.vibechat.data.room.ratelimit.OtpLimitEntity

@Database(
    entities = [MessageEntity::class, ChatRoomEntity::class, MessageLimitEntity::class, OtpLimitEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDAO(): ChatDAO
    abstract fun messageLimitDao(): MessageLimitDao
    abstract fun otpLimitDao(): OtpLimitDao
}