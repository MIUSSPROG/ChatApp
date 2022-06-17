package com.example.chatapp.data.remote

import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
): FirebaseRepository {
    override suspend fun verifyPhoneNumber(credential: PhoneAuthCredential): Flow<DataState<Boolean>> = flow {
        try {
            var isSuccessful = false
            auth.signInWithCredential(credential)
                .addOnSuccessListener { isSuccessful = true }
//                .addOnFailureListener { isSuccessful = false }
                .await()
            emit(DataState.Success(isSuccessful))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }


    override suspend fun getUsersForChat(userUUID: String): Flow<DataState<List<User>>> = flow {
        try {
            val users = db.reference.child("users").get().await().children
                .map { dataSnapshot ->
                    dataSnapshot.getValue(User::class.java)!! }
                .filter { user ->
                    user.uuid != userUUID
                }
            emit(DataState.Success(data = users))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }
}