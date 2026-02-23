package com.real.vibechat.presentation.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.real.vibechat.data.repository.ChatRepository
import com.real.vibechat.domain.models.ChatRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
): ViewModel() {

//    private val _chatRoomsList = MutableStateFlow<List<ChatRoom>>(emptyList())
//    val chatRoomsList : StateFlow<List<ChatRoom>> =_chatRoomsList
    val chatRoomsList: StateFlow<List<ChatRoom>> = chatRepository.observeChatRoomsList()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        observeChatsListFromFirebase()
//        loadChatsList()
    }

    private fun observeChatsListFromFirebase() {
        chatRepository.observeChatRoomsListFromFirebase()
    }

//    private fun loadChatsList() {
//        // observe chat list. -> ui observe from room  -> room observe
//        viewModelScope.launch {
//
//        }
//        chatRepository.observeChatRoomsList()
//            .collect {
//
//            }
//
//
//    }
}