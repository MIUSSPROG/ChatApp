package com.example.chatapp.domain.usecases

import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUsersForCharUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
)  {
    suspend operator fun invoke(userUUID: String): Flow<DataState<List<User>>> = flow {
        firebaseRepository.getUsersForChat(userUUID)
    }
}