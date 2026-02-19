package com.real.vibechat.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.real.vibechat.domain.models.User
import com.real.vibechat.domain.models.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    suspend fun fetchProfile(): UserProfile {
        val uid = auth.currentUser?.uid ?: throw Exception("Not logged in")

        val doc = firestore.collection("users")
            .document(uid)
            .get()
            .await()

        val user = doc.toObject(User::class.java)?: throw Exception("Profile not found")

        return UserProfile(
                name = user.username,
                imageUrl = user.profileImage,
                videoUrl = user.profileIntroVideo,
                caption = user.caption,
                createdAt = user.createdAt,
            )
    }



    suspend fun updateProfile(
        name: String,
        caption: String,
        imageUri: Uri?,
        introVideoUri: Uri?
    ) {
        val uid = auth.currentUser?.uid ?: throw Exception("Not logged in")

        var imageUrl: String? = null
        var introVideoUrl: String? = null

        val updates = mutableMapOf<String, Any>()

        // if name, caption is not empty . update it.
        if(name.isNotEmpty())  updates["username"] = name
        if(caption.isNotEmpty()) updates["caption"] = caption

        // Upload image if changed
        if (imageUri != null) {
            val ref = storage.reference.child("profile_images/$uid.jpg")
            ref.putFile(imageUri).await()
            imageUrl = ref.downloadUrl.await().toString()
        }

        // Upload video if changed
        if (introVideoUri != null) {
            val ref = storage.reference.child("intro_video/$uid.jpg")
            ref.putFile(introVideoUri).await()
            introVideoUrl = ref.downloadUrl.await().toString()
        }



        imageUrl?.let {
            updates["profileImage"] = it
        }
        introVideoUrl?.let {
            updates["profileIntroVideo"] = it
        }

        firestore.collection("users")
            .document(uid)
            .update(updates)
            .await()
    }
}
