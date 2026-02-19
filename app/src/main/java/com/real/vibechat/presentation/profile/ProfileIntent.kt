package com.real.vibechat.presentation.profile

import android.net.Uri

sealed class ProfileIntent {
    object LoadProfile : ProfileIntent()
    data class NameChanged(val name: String) : ProfileIntent()
    data class CaptionChanged(val caption: String) : ProfileIntent()
    data class ImageChanged(val uri: Uri) : ProfileIntent()
    data class IntroVideoSelected(val uri: Uri): ProfileIntent()
    object SaveProfile : ProfileIntent()
}

sealed class ProfileEffect {
    data class ShowToast(val message: String) : ProfileEffect()
}

