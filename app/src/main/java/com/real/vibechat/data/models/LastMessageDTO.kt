package com.real.vibechat.data.models

import com.google.firebase.Timestamp

data class LastMessageDTO(
    val body: String? = null,
    val chatRoomId: String? = null,
    val id: String? = null,
    val senderId: String? = null,
    val senderName: String? = null,
    val sentAt: Timestamp? = null
)