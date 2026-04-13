package com.vibechat.social.presentation.chat

import com.vibechat.social.domain.models.Message
import com.vibechat.social.domain.models.UserProfile

data class ChatState(
    val userProfile: UserProfile = UserProfile(),
    val messages: List<Message> = emptyList(),
    val input: String = "",
    val error: String? = null
)