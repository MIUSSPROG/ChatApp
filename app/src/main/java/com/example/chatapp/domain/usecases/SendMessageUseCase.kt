package com.example.chatapp.domain.usecases

import com.example.chatapp.domain.repository.FirebaseRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(messageTxt: String, receiverId: String) = firebaseRepository.sendMessage(messageTxt, receiverId)
}