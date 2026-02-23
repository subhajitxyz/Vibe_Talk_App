package com.real.vibechat.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.real.vibechat.data.room.MessageEntity
import com.real.vibechat.domain.models.Message
import com.real.vibechat.presentation.chat.MessageStatus

data class MessageDTO(
    val id: String = "",
    val chatRoomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val body: String = "",
    val sentAt: Timestamp? = null
)

fun MessageDTO.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "chatRoomId" to chatRoomId,
        "senderId" to senderId,
        "senderName" to senderName,
        "body" to body,
//        "sentAt" to FieldValue.serverTimestamp()
        "sentAt" to sentAt
    )
}


fun MessageDTO.toMessageEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        chatRoomId = chatRoomId,
        senderId = senderId,
        senderName = senderName,
        body = body,
        sentAt = sentAt?.toDate()?.time,
        messageStatus = MessageStatus.SENT
    )
}