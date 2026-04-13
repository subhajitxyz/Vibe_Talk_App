package com.vibechat.social.presentation.profile

import android.net.Uri
import com.vibechat.social.domain.models.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profile: UserProfile? = null,
    val editedName: String = "",
    val editedCaption: String = "",
    val editedNameError: String? = null,
    val editedCaptionError: String? = null,
    val editedImageUri: Uri? = null,
    val editedIntroVideoUri: Uri? = null,
    val error: String? = null
)
