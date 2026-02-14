package com.real.vibechat.presentation.auth

sealed class AuthResult {

    object Loading: AuthResult()
    object OtpSent: AuthResult()
    object ExistingUser: AuthResult()
    object NewUser: AuthResult()
    data class Error(val e: String): AuthResult()
}