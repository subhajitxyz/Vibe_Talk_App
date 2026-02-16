package com.real.vibechat.presentation.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.real.vibechat.data.repository.ExploreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val exploreRepository: ExploreRepository
): ViewModel() {

    private val _exploreUiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val exploreUiState: StateFlow<ExploreUiState> = _exploreUiState

    init {
        fetchNewUsers()
    }

    var isRefreshing by mutableStateOf(false)
        private set

    fun refreshUsers() {
        viewModelScope.launch {
            isRefreshing = true
            fetchNewUsers()
            isRefreshing = false
        }
    }

    private fun fetchNewUsers() {
        viewModelScope.launch {
            exploreRepository.fetchNewUsers()
                .collect { result ->
                    _exploreUiState.value = result
                }
        }

    }

}