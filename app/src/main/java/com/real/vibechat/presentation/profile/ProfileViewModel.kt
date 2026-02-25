package com.real.vibechat.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.real.vibechat.data.media.VideoTrimmer
import com.real.vibechat.data.repository.ProfileRepository
import com.real.vibechat.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val sessionManager: SessionManager,
    private val videoTrimmer: VideoTrimmer
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(isLoading = true))
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfile()
            is ProfileIntent.NameChanged -> {
                _state.update { it.copy(editedName = intent.name) }
                // update edited name error status
                updateEditedNameErrorStatus()
            }

            is ProfileIntent.CaptionChanged -> {
                _state.update { it.copy(editedCaption = intent.caption) }
                // update edited caption error status
                updateEditedCaptionErrorStatus()
            }

            is ProfileIntent.ImageChanged ->
                _state.update { it.copy(editedImageUri = intent.uri) }

            is ProfileIntent.IntroVideoSelected -> transformAndStoreVideo(intent.uri)

            is ProfileIntent.SaveProfile -> saveProfile()
        }
    }

    private fun transformAndStoreVideo(introVideo: Uri) {
        viewModelScope.launch {
            try {
                // transform long video --> into 10 sec
                val trimmedUri = videoTrimmer.trimTo10Seconds(introVideo)
                // store into ProfileUiState
                _state.update { it.copy(editedIntroVideoUri = trimmedUri) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error in video selection.") }
            }
        }
    }


    private fun updateEditedNameErrorStatus() {
        if(state.value.editedName.length > 3) {
            _state.update {
                it.copy(editedNameError = null)
            }
        }
    }

    private fun updateEditedCaptionErrorStatus() {
        if(state.value.editedCaption.length > 3) {
            _state.update {
                it.copy(editedCaptionError = null)
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val profile = repository.fetchProfile(sessionManager.getUserId())

                _state.update {
                    it.copy(
                        isLoading = false,
                        profile = profile
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(ProfileEffect.ShowToast(e.message ?: "Error"))
            }
        }
    }

    private fun saveProfile() {
        if(!validateInputs()) return
        viewModelScope.launch {
            val currentState = _state.value

            _state.update { it.copy(isSaving = true) }

            try {
                repository.updateProfile(
                    name = currentState.editedName.trim(),
                    caption = currentState.editedCaption.trim(),
                    imageUri = currentState.editedImageUri,
                    introVideoUri = currentState.editedIntroVideoUri
                )

                _effect.send(ProfileEffect.ShowToast("Profile Updated"))

                loadProfile() // refresh
                // clear input fields and error.
                clearInputFieldsAndError()
            } catch (e: Exception) {
                _effect.send(ProfileEffect.ShowToast(e.message ?: "Error"))
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun clearInputFieldsAndError() {
        _state.update {
            it.copy(
                editedNameError = null,
                editedCaptionError = null,
                editedName = "",
                editedCaption = "",
                error = null,
                editedImageUri = null,
                editedIntroVideoUri = null
            )
        }
    }

    private fun validateInputs(): Boolean {
        val editedName = state.value.editedName.trim()
        val editedCaption = state.value.editedCaption.trim()
        val editedImageUri = state.value.editedImageUri
        val editedIntroVideoUri = state.value.editedIntroVideoUri

        var isValid = true

        if(editedName.isNotEmpty() && editedName.length < 3) {
            isValid = false
            _state.update { it.copy(editedNameError = "Name must be at least of 3 character.") }
        }

        if(editedCaption.isNotEmpty() && editedCaption.length < 10) {
            isValid = false
            _state.update { it.copy(editedCaptionError = "Caption must be at least of 10 character.") }
        }

        if(editedName.isEmpty() && editedCaption.isEmpty() && editedImageUri == null && editedIntroVideoUri == null) {
            isValid = false
            _state.update { it.copy(error = "One field must be edited.") }
        }

        return isValid
    }


}
