package com.example.chatapp.presentation.screens.chat

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val attachFileUseCase: AttachFileUseCase,
    private val getReceiverStatusUseCase: GetReceiverStatusUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val textTypingUseCase: TextTypingUseCase,
    private val changeUserChatStatusUseCase: ChangeUserChatStatusUseCase,
    private val getMessagesUseCase: GetMessagesUseCase
): ViewModel() {

    private val _fileAttachedLiveData = MutableLiveData<DataState<Boolean>>()
    val fileAttachedLiveData: LiveData<DataState<Boolean>> = _fileAttachedLiveData

    private val _receiverStatusLiveData = MutableLiveData<DataState<String>>()
    val receiverStatusLiveData: LiveData<DataState<String>> = _receiverStatusLiveData

    private val _deletedMessageLiveData = MutableLiveData<DataState<Boolean>>()
    val deletedMessageLiveData: LiveData<DataState<Boolean>> = _deletedMessageLiveData

    private val _sendMessageLiveData = MutableLiveData<DataState<Boolean>>()
    val sendMessageLiveData: LiveData<DataState<Boolean>> = _sendMessageLiveData

    private val _messagesLiveData = MutableLiveData<DataState<List<Message>>>()
    val messagesLiveData: LiveData<DataState<List<Message>>> = _messagesLiveData

    fun attachFile(uri: Uri, receiverId: String){
        viewModelScope.launch {
            attachFileUseCase(uri, receiverId).onEach {
                _fileAttachedLiveData.value = it
            }.launchIn(this)
        }
    }

    fun getReceiverStatus(uid: String){
        viewModelScope.launch {
            getReceiverStatusUseCase(uid).onEach {
                _receiverStatusLiveData.value = it
            }.launchIn(this)
        }
    }

    fun deleteMessage(receiverId: String, message: Message){
        viewModelScope.launch {
            deleteMessageUseCase(receiverId, message).onEach {
                _deletedMessageLiveData.value = it
            }.launchIn(this)
        }
    }

    fun sendMessage(messageTxt: String, receiverId: String){
        viewModelScope.launch {
            sendMessageUseCase(messageTxt, receiverId).onEach {
                _sendMessageLiveData.value = it
            }.launchIn(this)
        }
    }

    fun textTyping(){
        viewModelScope.launch {
            textTypingUseCase().launchIn(this)
        }
    }

    fun changeUserChatStatus(status: String){
        viewModelScope.launch {
            changeUserChatStatusUseCase(status).launchIn(this)
        }
    }

    fun getMessages(receiverId: String){
        viewModelScope.launch {
            getMessagesUseCase(receiverId).onEach {
                _messagesLiveData.value = it
            }.launchIn(this)
        }
    }
}