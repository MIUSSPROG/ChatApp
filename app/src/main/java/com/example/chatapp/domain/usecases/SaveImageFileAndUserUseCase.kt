package com.example.chatapp.domain.usecases

import android.net.Uri
import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveImageFileAndUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(selectedImage: Uri?, name: String): Flow<DataState<Boolean>> =
        firebaseRepository.saveImageFileAndUser(selectedImage, name)
}