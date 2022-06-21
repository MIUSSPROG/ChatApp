package com.example.chatapp.presentation.screens.setup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.usecases.SaveImageFileAndUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupProfileViewModel @Inject constructor(
    private val saveImageFileAndUserUseCase: SaveImageFileAndUserUseCase
): ViewModel() {

    private val _resultLiveData = MutableLiveData<DataState<Boolean>>()
    val resultLiveData: LiveData<DataState<Boolean>> = _resultLiveData

    fun saveUserInfo(selectedImage: Uri?, name: String){
        viewModelScope.launch {
            saveImageFileAndUserUseCase(selectedImage, name).onEach { dataState ->
                _resultLiveData.value = dataState
            }.launchIn(this)
        }
    }
}