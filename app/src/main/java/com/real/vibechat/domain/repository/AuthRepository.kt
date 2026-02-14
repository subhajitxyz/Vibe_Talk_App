package com.real.vibechat.domain.repository

import android.app.Activity
import com.real.vibechat.presentation.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun sendOtp(phNumber: String, activity: Activity): Flow<AuthResult>
    fun verifyOtp(code: String): Flow<AuthResult>
}