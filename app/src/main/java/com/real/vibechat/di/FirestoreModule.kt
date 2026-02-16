package com.real.vibechat.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.real.vibechat.data.repository.OnboardRepoImpl
import com.real.vibechat.domain.repository.OnboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideOnboardRepository(
        firebaseStorage: FirebaseStorage,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): OnboardRepository {
        return OnboardRepoImpl(firestore, firebaseStorage, firebaseAuth)
    }
}