package com.example.chatapp.domain.repository

import android.net.Uri
import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun verifyPhoneNumber(credential: PhoneAuthCredential): Flow<DataState<Boolean>>

    suspend fun getUsersForChat(): Flow<DataState<List<User>>>

    suspend fun saveImageFileAndUser(selectedImage: Uri?, name: String): Flow<DataState<Boolean>>

    suspend fun changeUserChatStatus(status: String): Flow<DataState<Boolean>>

    suspend fun attachFile(uri: Uri, receiverId: String): Flow<DataState<Boolean>>

    suspend fun getReceiverStatus(uid: String): Flow<DataState<String>>

    suspend fun deleteMessage(receiverId: String, message: Message): Flow<DataState<Boolean>>

    suspend fun sendMessage(messageTxt: String, receiverId: String): Flow<DataState<Boolean>>

    suspend fun textTyping(): Flow<DataState<Unit>>

    suspend fun getMessages(receiverId: String): Flow<DataState<List<Message>>>
}