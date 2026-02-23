package com.real.vibechat.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MessageEntity::class, ChatRoomEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDAO(): ChatDAO
}