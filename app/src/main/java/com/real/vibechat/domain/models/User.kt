package com.real.vibechat.domain.models

import com.google.firebase.Timestamp

data class User(
    val username: String = "",
    val choices: List<String> = emptyList(),
    val profileImage: String? = null,
    val onboarded: Boolean = false,
    val createdAt: Timestamp? = null
)
