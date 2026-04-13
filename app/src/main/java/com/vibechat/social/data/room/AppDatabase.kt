package com.vibechat.social.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vibechat.social.data.room.ratelimit.MessageLimitDao
import com.vibechat.social.data.room.ratelimit.MessageLimitEntity
import com.vibechat.social.data.room.ratelimit.OtpLimitDao
import com.vibechat.social.data.room.ratelimit.OtpLimitEntity

@Database(
    entities = [MessageEntity::class, ChatRoomEntity::class, MessageLimitEntity::class, OtpLimitEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDAO(): ChatDAO
    abstract fun messageLimitDao(): MessageLimitDao
    abstract fun otpLimitDao(): OtpLimitDao
}