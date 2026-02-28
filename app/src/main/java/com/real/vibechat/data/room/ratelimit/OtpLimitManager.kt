package com.real.vibechat.data.room.ratelimit

import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpLimitManager @Inject constructor(
    private val dao: OtpLimitDao
) {

    private val MAX_DAILY_OTP = 5

    suspend fun canSendOtp(): Boolean {
        val today = LocalDate.now().toString()
        val existing = dao.getLimit()

        if (existing == null) {
            return true
        }

        return if (existing.date != today) {
            true // reset day
        } else {
            existing.count < MAX_DAILY_OTP
        }
    }

    suspend fun incrementOtpCount() {
        val today = LocalDate.now().toString()
        val existing = dao.getLimit()

        if (existing == null || existing.date != today) {
            dao.insert(
                OtpLimitEntity(
                    id = 0,
                    date = today,
                    count = 1
                )
            )
        } else {
            dao.insert(
                existing.copy(count = existing.count + 1)
            )
        }
    }
}