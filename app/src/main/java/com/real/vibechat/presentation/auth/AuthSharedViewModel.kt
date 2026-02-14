package com.real.vibechat.presentation.auth

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.real.vibechat.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthSharedViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {


    var phNumber by mutableStateOf("")
    var PhNumberError by mutableStateOf<String?>(null)

    var otp by mutableStateOf("")

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState : StateFlow<AuthResult?> = _authState


    fun sendOtp(activity: Activity) {
        if(!validatePhNumber()) return
        viewModelScope.launch {
            authRepository.sendOtp(phNumber, activity)
                .collect {
                    _authState.value = it
                }
        }
    }


    fun verifyOtp(code: String) {
        viewModelScope.launch {
            authRepository.verifyOtp(code)
                .collect {
                    _authState.value = it
                }
        }
    }

    private fun validatePhNumber(): Boolean {
        var isValid = true
        // 3️⃣ Phone Number Validation
        if (phNumber.isBlank()) {
            PhNumberError = "Phone number cannot be empty"
            isValid = false
        }
//        else if (!phNumber.matches(Regex("^[0-9]{10}$"))) {
//            PhNumberError = "Enter a valid 10-digit phone number"
//            isValid = false
//        }

        return isValid
    }
}