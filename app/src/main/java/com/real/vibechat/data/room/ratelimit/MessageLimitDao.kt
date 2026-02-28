package com.real.vibechat.data.room.ratelimit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageLimitDao {

    @Query("SELECT * FROM message_limit WHERE id = 0")
    suspend fun getLimit(): MessageLimitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(limit: MessageLimitEntity)

    @Query("DELETE FROM message_limit")
    suspend fun clear()
}