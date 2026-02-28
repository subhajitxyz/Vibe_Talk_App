package com.real.vibechat.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.real.vibechat.data.repository.AuthRepoImpl
import com.real.vibechat.data.room.ratelimit.OtpLimitManager
import com.real.vibechat.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        otpLimitManager: OtpLimitManager
    ): AuthRepository =
        AuthRepoImpl(firebaseAuth, firestore, otpLimitManager)
}
