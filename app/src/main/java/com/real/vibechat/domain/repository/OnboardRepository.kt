package com.real.vibechat.domain.repository

import android.net.Uri
import com.real.vibechat.presentation.onboarding.OnboardResult
import kotlinx.coroutines.flow.Flow

interface OnboardRepository {
    fun onboardUser(
        userImage: Uri?,
        username: String,
        userChoices: List<String>
    ): Flow<OnboardResult>
}