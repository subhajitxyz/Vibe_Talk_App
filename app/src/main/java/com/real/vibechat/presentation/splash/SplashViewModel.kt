package com.real.vibechat.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.real.vibechat.presentation.onboarding.StartDestination
import com.real.vibechat.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _destination = MutableStateFlow<StartDestination?>(null)
    val destination: StateFlow<StartDestination?> = _destination

    init {
        checkUser()
    }

    private fun checkUser() {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()

            if (userId == null) {
                delay(1500)
                _destination.value = StartDestination.AUTH
                return@launch
            }

            val delayJob = async { delay(1500) }
            val userJob = async { checkUserFromFirestore(userId) }

            delayJob.await()
            val result = userJob.await()

            _destination.value = result
        }
    }

    private suspend fun checkUserFromFirestore(userId: String): StartDestination {
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (doc.exists() && doc.getBoolean("onboarded") == true) {
                StartDestination.HOME
            } else {
                StartDestination.ONBOARDING
            }
        } catch (e: Exception) {
            StartDestination.AUTH
        }
    }

}
