package com.example.chatapp.domain.usecases

import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.repository.FirebaseRepository
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VerifyPhoneNumberUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(credential: PhoneAuthCredential): Flow<DataState<Boolean>> =
        firebaseRepository.verifyPhoneNumber(credential)
}