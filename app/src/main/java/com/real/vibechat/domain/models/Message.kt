package com.real.vibechat.domain.models

import com.real.vibechat.data.room.MessageEntity
import com.real.vibechat.presentation.chat.MessageStatus
import com.real.vibechat.presentation.chat.SenderType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Message(
    val id: String,
    val chatRoomId: String,
    val senderId: String,
    val senderType: SenderType,
    val senderName: String,
    val body: String,
    val sentAt: Long?,
    val messageStatus: MessageStatus
)


fun Message.toMessageEntity(): MessageEntity {
    return MessageEntity(
        id = this.id,
        chatRoomId = this.chatRoomId,
        senderId = this.senderId,
        senderName = this.senderName,
        body = this.body,
        sentAt = this.sentAt,
        messageStatus = messageStatus
    )
}

fun Long.toFormattedTime(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}



