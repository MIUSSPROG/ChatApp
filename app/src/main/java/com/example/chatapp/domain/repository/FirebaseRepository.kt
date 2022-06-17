package com.example.chatapp.domain.repository

import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.model.User
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun verifyPhoneNumber(credential: PhoneAuthCredential): Flow<DataState<Boolean>>

    suspend fun getUsersForChat(userUUID: String): Flow<DataState<List<User>>>
}