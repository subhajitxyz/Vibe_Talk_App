package com.vibechat.social.domain.repository

import android.net.Uri
import com.vibechat.social.presentation.onboarding.OnboardResult
import kotlinx.coroutines.flow.Flow

interface OnboardRepository {
    fun onboardUser(
        userImage: Uri?,
        username: String,
        userChoices: List<String>
    ): Flow<OnboardResult>
}