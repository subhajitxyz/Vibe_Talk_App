package com.real.vibechat.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.real.vibechat.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.real.vibechat.presentation.auth.AuthResult
import kotlinx.coroutines.channels.awaitClose
import java.util.concurrent.TimeUnit

class AuthRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private var verificationId: String? = null

    override fun sendOtp(
        phone: String,
        activity: Activity
    ): Flow<AuthResult> = callbackFlow {

        trySend(AuthResult.Loading)

        val callbacks = object :
            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                firebaseAuth.signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(AuthResult.Error("Verification Failed"))
            }

            override fun onCodeSent(
                verId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = verId
                trySend(AuthResult.OtpSent)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose { }
    }

    override fun verifyOtp(code: String): Flow<AuthResult> = callbackFlow {

        trySend(AuthResult.Loading)

        val credential =
            PhoneAuthProvider.getCredential(verificationId ?: "", code)

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {

                val uid = firebaseAuth.currentUser?.uid ?: ""

                firestore.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { document ->

                        if (document.exists()) {
                            trySend(AuthResult.ExistingUser)
                        } else {
                            trySend(AuthResult.NewUser)
                        }
                    }.addOnFailureListener {
                        trySend(AuthResult.Error(it.message ?: "USer document add error"))
                    }
            }
            .addOnFailureListener {
                trySend(AuthResult.Error("Invalid OTP"))
            }

        awaitClose { }
    }
}
