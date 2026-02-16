package com.real.vibechat.presentation.onboarding

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.real.vibechat.domain.repository.OnboardRepository
import com.real.vibechat.presentation.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardRepository: OnboardRepository
): ViewModel() {

    val predefinedChoiceList by lazy {
        listOf(
            "Gaming",
            "Music",
            "Travel",
            "Photography",
            "Fitness",
            "Movies",
            "Reading",
            "Food",
            "Art",
            "Startups",
            "Anime",
            "Sports",
            "Nature",
            "Technology"
        )
    }

    var currentOnboardScreen by mutableStateOf(OnboardScreenType.OnboardScreenOne)

    var userName by mutableStateOf("")
    var userNameError by mutableStateOf<String?>(null)
    var userImageUri by mutableStateOf<Uri?>(null)

    val selectedItemsIndex = mutableStateListOf<Int>()
    var selectedItemChoiceError by mutableStateOf<Boolean>(false)

    private val _userOnboardResult = MutableStateFlow<OnboardResult?>(null)
    val userOnboardResult: StateFlow<OnboardResult?> = _userOnboardResult

    fun canSelectedItem(): Boolean {
        return selectedItemsIndex.size < 5
    }

    fun isItemSelected(itemIndex: Int): Boolean {
        return selectedItemsIndex.contains(itemIndex)
    }

    fun selectItem(itemIndex: Int) {
        if(canSelectedItem() && !isItemSelected(itemIndex)) {
            selectedItemsIndex.add(itemIndex)
            if(validateSelectedChoices()) selectedItemChoiceError = false
        }
    }

    fun unSelectItem(itemIndex: Int) {
        if(isItemSelected(itemIndex)) {
            selectedItemsIndex.remove(itemIndex)
        }
    }

    fun setImageUri(uri: Uri) {
        userImageUri = uri
    }

    fun validateUserName(): Boolean {
        if (userName.trim().length < 3)  {
            userNameError = "Name should be at least of 3 characters."
            return false
        }
        return true
    }

    private fun validateSelectedChoices(): Boolean {
        return selectedItemsIndex.size >= 3
    }

    fun moveToOnboardingScreenTwo() {
        if (!validateUserName()) return
        currentOnboardScreen = OnboardScreenType.OnboardScreenTwo

    }

    fun onboardUserProfile()  {
        if(!validateSelectedChoices()) {
            selectedItemChoiceError = true
            return
        }
        val userChoiceList = selectedItemsIndex.map { it ->
            predefinedChoiceList[it]
        }

        viewModelScope.launch {
            onboardRepository.onboardUser(userImageUri, userName, userChoiceList)
                .collect { result ->
                    _userOnboardResult.value = result
                }
        }


    }
}