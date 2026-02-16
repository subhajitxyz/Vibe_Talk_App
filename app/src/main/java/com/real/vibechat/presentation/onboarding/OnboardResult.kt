package com.real.vibechat.presentation.onboarding

sealed class OnboardResult {
    object Loading: OnboardResult()
    object Success: OnboardResult()
    data class Error(val e: String) : OnboardResult()
}