package com.example.chatapp.domain.usecases

import android.net.Uri
import com.example.chatapp.domain.repository.FirebaseRepository
import javax.inject.Inject

class AttachFileUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(uri: Uri, receiverId: String) = firebaseRepository.attachFile(uri, receiverId)
}