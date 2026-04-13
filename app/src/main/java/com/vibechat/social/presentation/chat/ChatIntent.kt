package com.vibechat.social.presentation.chat

sealed class ChatIntent {
    data class SendMessage(val input: String) : ChatIntent()
}