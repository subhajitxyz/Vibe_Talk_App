package com.real.vibechat.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.real.vibechat.domain.models.ChatRoom
import kotlin.String

@Entity(tableName = "chat_rooms")
data class ChatRoomEntity(
    @PrimaryKey
    val chatroomId: String,
    val friendId: String,
    val messageId: String,
    val friendProfileUrl: String?,
    val friendName: String,
    val lastMessage: String,
    val sentAt: Long?
)

fun ChatRoomEntity.toDomain(): ChatRoom {
    return ChatRoom (
        chatroomId = chatroomId,
        friendId = friendId,
        friendName = friendName,
        friendProfileUrl = friendProfileUrl,
        messageId = messageId,
        lastMessage = lastMessage,
        sentAt = sentAt
    )
}