package com.example.chatapp.domain.usecases

import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.repository.FirebaseRepository
import javax.inject.Inject


class DeleteMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(receiverId: String, message: Message) = firebaseRepository.deleteMessage(receiverId, message)
}