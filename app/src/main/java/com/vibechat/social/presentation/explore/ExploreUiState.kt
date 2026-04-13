package com.vibechat.social.presentation.explore

import com.vibechat.social.domain.models.User

sealed class ExploreUiState {
    object Loading: ExploreUiState()
    data class Success(val users: List<User>): ExploreUiState()
    data class Error(val e: String): ExploreUiState()
}