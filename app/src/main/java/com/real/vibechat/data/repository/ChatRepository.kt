package com.real.vibechat.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.real.vibechat.data.SendMessageWorker
import com.real.vibechat.data.models.ChatRoomDTO
import com.real.vibechat.data.models.MessageDTO
import com.real.vibechat.data.models.toChatRoomEntity
import com.real.vibechat.data.models.toMessageEntity
import com.real.vibechat.data.room.ChatDAO
import com.real.vibechat.data.room.ratelimit.MessageLimitManager
import com.real.vibechat.data.room.toDomain
import com.real.vibechat.di.ApplicationScope
import com.real.vibechat.domain.models.ChatRoom
import com.real.vibechat.domain.models.Message
import com.real.vibechat.domain.models.toEntity
import com.real.vibechat.domain.models.toMessageEntity
import com.real.vibechat.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.String

class ChatRepository @Inject constructor(
    private val chatDAO: ChatDAO,
    private val messageLimitManager: MessageLimitManager,
    private val workManager: WorkManager,
    private val sessionManager: SessionManager,
    private val firestore: FirebaseFirestore,
    private val profileRepository: ProfileRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) {

    private val currentUserId = checkNotNull(sessionManager.getUserId())
    private var chatRoomsListener: ListenerRegistration? = null

    fun observeFirebaseMessages(chatRoomId: String) {
        firestore.collection("chats")
            .document(chatRoomId)
            .collection("messages")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val messages = snapshot?.documentChanges
                    ?.filter {
                        it.type == DocumentChange.Type.ADDED ||
                                it.type == DocumentChange.Type.MODIFIED
                    }
                    ?.map {
                        it.document.toObject(MessageDTO::class.java)
                            .toMessageEntity()
                    }

                if (!messages.isNullOrEmpty()) {
                    applicationScope.launch {
                        chatDAO.insertAll(messages)
                    }
                } }

    }

    fun observeMessages(chatRoomId: String): Flow<List<Message>> =
        chatDAO.observeMessages(chatRoomId)
            .map { list ->
                list.map {
                    it.toDomain(sessionManager.getUserId()!!)
                }
            }

    suspend fun sendMessage(message: Message, receiverId: String) {
        if(!messageLimitManager.canSendMessage()) throw Exception("Daily Message Limit exceeded. Try on next day.")
        val messageEntity = message.toMessageEntity()
        chatDAO.insert(messageEntity)

        saveChatRoomInLocalDB(message, receiverId)

        // 2️⃣ Enqueue background sync
        enqueueSendMessageWorker(message.id, receiverId)
    }

    private suspend fun saveChatRoomInLocalDB(message: Message, receiverId: String) {
        // also update the chatrooms list first
        val chatRoomId = message.chatRoomId
        val friendId = receiverId
        val messageId = message.id
        val lastMessage = message.body
        val sentAt = message.sentAt

        if(!chatDAO.chatRoomExists(chatRoomId)) {
            // it is not first time.
            try {
                val friendProfile = profileRepository.fetchProfile(friendId)
                val friendName = friendProfile.name
                val friendProfileUrl = friendProfile.imageUrl

                val chatRoom = ChatRoom (
                    chatroomId = chatRoomId,
                    friendId = friendId,
                    messageId = messageId,
                    friendProfileUrl = friendProfileUrl,
                    friendName = friendName,
                    lastMessage = lastMessage,
                    sentAt = sentAt
                )
                chatDAO.insertChatRoom(chatRoom.toEntity())

            } catch (e: Exception) {
                e.message?.let { Log.d("Error", it) }
            }

        } else {
            chatDAO.updateLastMessage(
                chatRoomId,
                messageId = messageId,
                lastMessage = lastMessage,
                sentAt = sentAt
            )

        }
    }

    private fun enqueueSendMessageWorker(messageId: String, receiverId: String) {

        val work = OneTimeWorkRequestBuilder<SendMessageWorker>()
            .setInputData(
                workDataOf("messageId" to messageId, "receiverId" to receiverId)
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueue(work)
    }




    // Observe ChatRoom list from local db.
    fun observeChatRoomsList(): Flow<List<ChatRoom>> =
        chatDAO.observeChatRooms()
            .map { list ->
                list.map { it ->
                    it.toDomain()
                }
            }


    /* This function help to observe chat room list from firebase and update local db,
        if last message changes.

        We need to store friend profile url and profile name. so we needed to fetch this from
        firebase separately.
        why we have tried to reduce the unnecessary profile fetch call for every chatroom changes.
        if chatroom changes for first time -> then fetch profile image , name and store in local.
        if not first time -> only store last message and other info.

    * */

    /* Future plan -> save both user profile info in chatroom in firebase when first time messages.
       In that case , we can avoid unnecessary profile fetch call.
    * */
    fun observeChatRoomsListFromFirebase() {
        val currentUserId = checkNotNull(sessionManager.getUserId())

        chatRoomsListener?.remove()

        chatRoomsListener = firestore.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e("ChatRepository", "ChatRooms listener error", error)
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                applicationScope.launch {
                    snapshot.documentChanges.forEach { change ->

                        val dto = change.document
                            .toObject(ChatRoomDTO::class.java)
                            ?: return@forEach

                        val chatRoomId = change.document.id

                        when (change.type) {

                            DocumentChange.Type.ADDED -> {

                                val exists = chatDAO.chatRoomExists(chatRoomId)
                                if (!exists) {
                                    val friendId = dto.participants
                                        .firstOrNull { it != currentUserId }
                                        ?: return@forEach

                                    val friendProfile = profileRepository.fetchProfile(friendId)
                                    val entity = dto.toChatRoomEntity(
                                        friendId = friendId,
                                        friendName = friendProfile.name,
                                        friendProfileUrl = friendProfile.imageUrl
                                    )

                                    chatDAO.insertChatRoom(entity)
                                }
                            }

                            DocumentChange.Type.MODIFIED -> {
                                val messageId = checkNotNull(dto.lastMessage?.id)
                                val messageBody = checkNotNull(dto.lastMessage?.body)
                                val sentAt = dto.lastMessage?.sentAt?.toDate()?.time

                                chatDAO.updateLastMessage(
                                    chatRoomId,
                                    messageId = messageId,
                                    lastMessage = messageBody,
                                    sentAt = sentAt
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
    }



}