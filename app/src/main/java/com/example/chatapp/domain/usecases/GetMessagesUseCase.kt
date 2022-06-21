package com.example.chatapp.domain.usecases

import com.example.chatapp.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(receiverId: String) = firebaseRepository.getMessages(receiverId)
}