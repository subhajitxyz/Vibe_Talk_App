package com.vibechat.social.presentation.onboarding

sealed class OnboardResult {
    object Loading: OnboardResult()
    object Success: OnboardResult()
    data class Error(val e: String) : OnboardResult()
}