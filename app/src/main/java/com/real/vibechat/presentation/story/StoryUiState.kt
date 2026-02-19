package com.real.vibechat.presentation.story

data class StoryUiState(
    val isLoading: Boolean = false,
    val isFinished: Boolean = false,
    val content: StoryType = StoryType.IMAGE
)