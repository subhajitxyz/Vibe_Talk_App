package com.real.vibechat.data.room.ratelimit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OtpLimitDao {

    @Query("SELECT * FROM otp_limit WHERE id = 0")
    suspend fun getLimit(): OtpLimitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(limit: OtpLimitEntity)
}