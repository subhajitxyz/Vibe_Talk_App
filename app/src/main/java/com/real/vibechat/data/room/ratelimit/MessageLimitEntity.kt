package com.real.vibechat.data.room.ratelimit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_limit")
data class MessageLimitEntity(
    @PrimaryKey val id: Int = 0,   // Always 0 (single row)
    val date: String,
    val count: Int
)