package com.example.chatapp.presentation.screens.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.usecases.VerifyPhoneNumberUseCase
import com.google.firebase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class OTPViewModel @Inject constructor(
    private val verifyPhoneNumberUserCase: VerifyPhoneNumberUseCase
): ViewModel(){

    private val _authResultLiveData = MutableLiveData<DataState<Boolean>>()
    val authResultLiveData: LiveData<DataState<Boolean>> = _authResultLiveData

    fun verifyPhoneNumber(credential: PhoneAuthCredential){
        viewModelScope.launch {
            verifyPhoneNumberUserCase(credential).onEach { dataState ->
                _authResultLiveData.value = dataState
            }.launchIn(this)
        }
    }

}