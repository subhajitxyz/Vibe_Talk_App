package com.real.vibechat.presentation.explore

import com.real.vibechat.domain.models.User

sealed class ExploreUiState {
    object Loading: ExploreUiState()
    data class Success(val users: List<User>): ExploreUiState()
    data class Error(val e: String): ExploreUiState()
}