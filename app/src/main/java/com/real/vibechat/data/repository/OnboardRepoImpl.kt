package com.real.vibechat.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.real.vibechat.domain.repository.OnboardRepository
import com.real.vibechat.presentation.onboarding.OnboardResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OnboardRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth
): OnboardRepository {

    override fun onboardUser(
        userImage: Uri?,
        username: String,
        userChoices: List<String>
    ): Flow<OnboardResult> = flow {
        emit(OnboardResult.Loading)
        emit(onboardUserProfile(userImage, username, userChoices))
    }


    private suspend fun onboardUserProfile(
        userImage: Uri?,
        username: String,
        userChoices: List<String>
    ): OnboardResult {

        val userId = firebaseAuth.currentUser?.uid
            ?: return OnboardResult.Error("User not authenticated")

        return try {

            var imageUrl: String? = null

            // 1️⃣ Upload image if exists
            if (userImage != null) {
                val storageRef = firebaseStorage.reference
                    .child("profile_images/$userId.jpg")

                storageRef.putFile(userImage).await()
                imageUrl = storageRef.downloadUrl.await().toString()
            }

            // 2️⃣ Create user data map
            val userData = hashMapOf(
                "username" to username,
                "choices" to userChoices,
                "profileImage" to imageUrl,
                "videoUrl" to null,
                "onboarded" to true,
                "caption" to "",
                "createdAt" to FieldValue.serverTimestamp()
            )

            // 3️⃣ Save everything in one write
            firestore.collection("users")
                .document(userId)
                .set(userData)
                .await()

            OnboardResult.Success

        } catch (e: Exception) {
            OnboardResult.Error(e.message ?: "Onboarding failed")
        }
    }

}