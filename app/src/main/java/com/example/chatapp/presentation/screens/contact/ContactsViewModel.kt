package com.example.chatapp.presentation.screens.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.usecases.ChangeUserChatStatusUseCase
import com.example.chatapp.domain.usecases.GetUsersForCharUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getUsersForChatUseCase: GetUsersForCharUseCase,
    private val changeUserChatStatusUseCase: ChangeUserChatStatusUseCase
): ViewModel() {

    private val _usersLiveData = MutableLiveData<DataState<List<User>>>()
    val usersLiveData: LiveData<DataState<List<User>>> = _usersLiveData

    fun getUsers(){
        viewModelScope.launch {
            getUsersForChatUseCase().onEach { dataState ->
                _usersLiveData.value = dataState
            }.launchIn(this)
        }
    }

    fun changeUserChatStatus(status: String){
        viewModelScope.launch {
            changeUserChatStatusUseCase(status).launchIn(this)
        }
    }
}