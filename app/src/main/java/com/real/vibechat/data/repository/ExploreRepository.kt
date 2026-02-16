package com.real.vibechat.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.real.vibechat.domain.models.User
import com.real.vibechat.presentation.explore.ExploreUiState
import com.real.vibechat.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExploreRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val firestore: FirebaseFirestore
) {

    fun fetchNewUsers(): Flow<ExploreUiState> = flow{
        // first fetch -> current use choices.
        // second fetch -> users with the same choices from firebase.
        emit(ExploreUiState.Loading)
        emit(fetchMatchingUser())

    }

    private suspend fun fetchMatchingUser(): ExploreUiState {
        val userId = sessionManager
            .getUserId()?: return ExploreUiState.Error("Invalid User Id")

        try {
            val currentUserDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if(!currentUserDoc.exists()) return ExploreUiState.Error("Invalid User")

            val currentUserChoices = currentUserDoc.get("choices") as? List<String> ?: emptyList()

            // Query users with at least one matching choices.
            val querySnapshot = firestore.collection("users")
                .whereArrayContainsAny("choices", currentUserChoices)
                .get()
                .await()


            // 3️⃣ Map to user model & remove current user
            val matchedUsers = querySnapshot.documents
                .filter { it.id != userId }
                .mapNotNull { doc ->
                    doc.toObject(User::class.java)
                }

            return ExploreUiState.Success(matchedUsers)

        } catch (e: Exception) {
            return ExploreUiState.Error(e.message ?: "Failed to fetch users")
        }
    }
}