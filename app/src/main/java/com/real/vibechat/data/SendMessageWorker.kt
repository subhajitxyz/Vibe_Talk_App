package com.real.vibechat.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.real.vibechat.data.models.toFirestoreMap
import com.real.vibechat.data.room.ChatDAO
import com.real.vibechat.data.room.toMessageDTO
import com.real.vibechat.presentation.chat.MessageStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class SendMessageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val chatDAO: ChatDAO,
    private val firestore: FirebaseFirestore,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val messageId = inputData.getString("messageId") ?: return Result.failure()
        val receiverId = inputData.getString("receiverId") ?: return Result.failure()

        val message = chatDAO.getMessageById(messageId)

        return try {

            val firestoreMessageMap = message.toMessageDTO().toFirestoreMap()

            val chatDocRef = firestore.collection("chats")
                .document(message.chatRoomId)

            // 1️⃣ Send message
            chatDocRef.collection("messages")
                .document(message.id)
                .set(firestoreMessageMap)
                .await()

            // 2️⃣ Update chat document (safe merge)
            val chatData = mapOf(
                "participants" to listOf(message.senderId, receiverId),
                "lastMessage" to firestoreMessageMap
            )

            chatDocRef.set(chatData, SetOptions.merge()).await()

            // 3️⃣ Update local DB
            chatDAO.updateStatus(message.id, MessageStatus.SENT)

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}