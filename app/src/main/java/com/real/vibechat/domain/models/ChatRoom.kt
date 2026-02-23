package com.real.vibechat.domain.models

import com.real.vibechat.data.room.ChatRoomEntity

data class ChatRoom (
    val chatroomId: String,
    val friendId: String,
    val messageId: String,
    val friendProfileUrl: String?,
    val friendName: String,
    val lastMessage: String,
    val sentAt: Long?
)

fun ChatRoom.toEntity(): ChatRoomEntity {
    return ChatRoomEntity (
        chatroomId = chatroomId,
        friendId = friendId,
        friendName = friendName,
        friendProfileUrl = friendProfileUrl,
        messageId = messageId,
        lastMessage = lastMessage,
        sentAt = sentAt
    )
}
