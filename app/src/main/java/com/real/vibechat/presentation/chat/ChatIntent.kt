package com.real.vibechat.presentation.chat

sealed class ChatIntent {
    data class SendMessage(val input: String) : ChatIntent()
}