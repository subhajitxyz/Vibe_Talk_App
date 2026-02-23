package com.real.vibechat.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.real.vibechat.data.models.MessageDTO
import com.real.vibechat.domain.models.Message
import com.real.vibechat.presentation.chat.MessageStatus
import com.real.vibechat.presentation.chat.SenderType
import java.util.Date

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatRoomId: String,
    val senderId: String,
    val senderName: String,
    val body: String,
    val sentAt: Long?,
    val messageStatus: MessageStatus
)

fun MessageEntity.toDomain(currentUserId: String): Message {
    return Message(
        id = id,
        chatRoomId = chatRoomId,
        senderId = senderId,
        senderName = senderName,
        senderType = if(currentUserId == senderId) {
            SenderType.SELF
        } else {
            SenderType.OTHER
        },
        body = body,
        sentAt = sentAt,
        messageStatus = messageStatus
    )
}

fun MessageEntity.toMessageDTO(): MessageDTO {
    return MessageDTO(
        id = this.id,
        chatRoomId = this.chatRoomId,
        senderId = this.senderId,
        senderName = this.senderName,
        body = this.body,
        sentAt = sentAt?.let { Timestamp(Date(it)) }
    )
}