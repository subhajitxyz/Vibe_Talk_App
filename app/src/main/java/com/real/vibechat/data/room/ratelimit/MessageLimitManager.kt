package com.real.vibechat.data.room.ratelimit

import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageLimitManager @Inject constructor(
    private val messageLimitDao: MessageLimitDao
) {

    private val MAX_DAILY_MESSAGES = 100

    suspend fun canSendMessage(): Boolean {

        val today = LocalDate.now().toString()
        val existing = messageLimitDao.getLimit()

        if (existing == null) {
            messageLimitDao.insert(
                MessageLimitEntity(
                    id = 0,
                    date = today,
                    count = 1
                )
            )
            return true
        }

        return if (existing.date != today) {
            // 🔥 RESET here
            messageLimitDao.insert(
                MessageLimitEntity(
                    id = 0,
                    date = today,
                    count = 1
                )
            )
            true
        } else {
            if (existing.count >= MAX_DAILY_MESSAGES) {
                false
            } else {
                messageLimitDao.insert(
                    existing.copy(count = existing.count + 1)
                )
                true
            }
        }
    }
}