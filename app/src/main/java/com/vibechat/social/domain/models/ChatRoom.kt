package com.vibechat.social.domain.models

import com.vibechat.social.data.room.ChatRoomEntity

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
