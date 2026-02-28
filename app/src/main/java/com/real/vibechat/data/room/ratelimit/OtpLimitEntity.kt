package com.real.vibechat.data.room.ratelimit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "otp_limit")
data class OtpLimitEntity(
    @PrimaryKey val id: Int = 0,
    val date: String,
    val count: Int
)