package com.real.vibechat.domain.models

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class UserProfile(
    val name: String = "",
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val caption: String? = null,
    val createdAt: Timestamp? = null
)


fun Timestamp.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(this.toDate())
}

