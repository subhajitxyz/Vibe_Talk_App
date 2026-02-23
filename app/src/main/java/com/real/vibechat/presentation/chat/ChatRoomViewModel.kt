package com.real.vibechat.presentation.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.real.vibechat.data.repository.ChatRepository
import com.real.vibechat.data.repository.ProfileRepository
import com.real.vibechat.domain.models.Message
import com.real.vibechat.domain.models.UserProfile
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sessionManager: SessionManager,
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val otherUserId: String =
        checkNotNull(savedStateHandle["userId"])

    private val currentUserId: String =
        checkNotNull(sessionManager.getUserId())

    private var currentUserProfile: UserProfile? = null

    private val chatRoomId: String =
        createChatRoomId(currentUserId, otherUserId)

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        fetchCurrentUserProfile()
        startRemoteListener()
        fetchOtherUserProfile()
        observeMessages()
    }

    private fun fetchCurrentUserProfile() {
        viewModelScope.launch {
            try {
                currentUserProfile = profileRepository.fetchProfile(currentUserId)
            } catch (e: Exception) {
                e.message?.let { Log.e("Error",it) }
            }
        }
    }

    private fun startRemoteListener() {
       chatRepository.observeFirebaseMessages(chatRoomId)
    }


    private fun fetchOtherUserProfile() {
        viewModelScope.launch {
            try {
                val profile = profileRepository.fetchProfile(otherUserId)
                _state.update { it.copy(userProfile = profile) }
            } catch (e: Exception) {
                e.message?.let { Log.e("Error",it) }
            }
        }

    }

    private fun observeMessages() {
        chatRepository.observeMessages(chatRoomId)
            .onEach { messages ->
                _state.update { it.copy(messages = messages) }
            }
            .launchIn(viewModelScope)
    }

    private fun createChatRoomId(
        user1: String,
        user2: String
    ): String {
        return if (user1 < user2) {
            "$user1-$user2"
        } else {
            "$user2-$user1"
        }
    }

    fun handleInput(input: String) {
        _state.update { it.copy(input = input) }
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.SendMessage -> {
                viewModelScope.launch {
                    chatRepository.sendMessage(
                        Message(
                            id = UUID.randomUUID().toString(),
                            chatRoomId = chatRoomId,
                            senderId = sessionManager.getUserId()!!,
                            senderName = "USer Name",
                            senderType = SenderType.SELF,
                            body = intent.input,
                            sentAt = System.currentTimeMillis(),
                            messageStatus = MessageStatus.PENDING
                        ),
                        receiverId = otherUserId
                    )

                    _state.update { it.copy(input = "") }
                }
            }
        }
    }
}