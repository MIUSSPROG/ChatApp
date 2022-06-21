package com.example.chatapp.domain.usecases

import com.example.chatapp.domain.repository.FirebaseRepository
import javax.inject.Inject

class ChangeUserChatStatusUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
){
    suspend operator fun invoke(status: String) = firebaseRepository.changeUserChatStatus(status)
}