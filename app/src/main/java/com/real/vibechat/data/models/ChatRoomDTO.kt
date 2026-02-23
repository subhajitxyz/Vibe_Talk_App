package com.real.vibechat.data.models

import com.real.vibechat.data.room.ChatRoomEntity
import kotlin.String

data class ChatRoomDTO(
    val lastMessage: LastMessageDTO? = null,
    val participants: List<String> = emptyList()
)

fun ChatRoomDTO.toChatRoomEntity(friendId: String, friendProfileUrl: String?, friendName: String): ChatRoomEntity {
    return ChatRoomEntity(
        chatroomId = lastMessage?.chatRoomId?: "",
        friendId = friendId,
        messageId = lastMessage?.id?: "",
        friendProfileUrl = friendProfileUrl,
        friendName = friendName,
        lastMessage = lastMessage?.body?: "",
        sentAt = lastMessage?.sentAt?.toDate()?.time
    )
}