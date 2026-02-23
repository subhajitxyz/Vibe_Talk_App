package com.real.vibechat.presentation.chat

import com.real.vibechat.domain.models.Message
import com.real.vibechat.domain.models.UserProfile

data class ChatState(
    val userProfile: UserProfile = UserProfile(),
    val messages: List<Message> = emptyList(),
    val input: String = ""
)