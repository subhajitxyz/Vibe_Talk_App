package com.real.vibechat.domain.models

import com.google.firebase.Timestamp


// It is a working as Dto model. in future , i will change its name to UserDTO
data class User(
    val userId: String = "",
    val username: String = "",
    val choices: List<String> = emptyList(),
    val profileImage: String? = null,
    val profileIntroVideo: String? = null,
    val onboarded: Boolean = false,
    val caption: String = "",
    val createdAt: Timestamp? = null
)
