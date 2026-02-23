package com.real.vibechat.utils

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

}