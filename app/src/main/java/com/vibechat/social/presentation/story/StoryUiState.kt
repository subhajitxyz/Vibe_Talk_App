package com.vibechat.social.presentation.story

data class StoryUiState(
    val isLoading: Boolean = false,
    val isFinished: Boolean = false,
    val content: StoryType = StoryType.IMAGE
)