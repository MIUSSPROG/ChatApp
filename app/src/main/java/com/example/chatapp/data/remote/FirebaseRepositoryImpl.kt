package com.example.chatapp.data.remote

import android.net.Uri
import android.os.Handler
import com.example.chatapp.core.util.DataState
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase,
    private val storage: FirebaseStorage
): FirebaseRepository {
    override suspend fun verifyPhoneNumber(credential: PhoneAuthCredential): Flow<DataState<Boolean>> = flow {
        try {
            var isSuccessful = false
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    isSuccessful = true
                }
//                .addOnFailureListener { isSuccessful = false }
                .await()
            emit(DataState.Success(isSuccessful))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }


    override suspend fun saveImageFileAndUser(selectedImage: Uri?, name: String): Flow<DataState<Boolean>> = flow {
        try {
            val reference = storage.reference.child("Profile").child(auth.uid!!)
            var imageUri: String = "No image"
            if (selectedImage != null) {
                reference.putFile(selectedImage).await()
                imageUri = reference.downloadUrl.await().toString()
            }
            val user = User(uuid = auth.uid, name = name, auth.currentUser!!.phoneNumber, profileImage = imageUri)
            db.reference.child("users").child(auth.uid!!).setValue(user).await()
            emit(DataState.Success())
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun changeUserChatStatus(status: String) = flow<DataState<Boolean>>{
        try {
            db.reference.child("Presence")
                .child(auth.uid!!).setValue(status)
            emit(DataState.Success())
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun attachFile(uri: Uri, receiverId: String): Flow<DataState<Boolean>> = flow {
        try {
            val calendar = Calendar.getInstance()
            val reference = storage.reference.child("chats").child(calendar.timeInMillis.toString())
            reference.putFile(uri).await()
            val filePath = reference.downloadUrl.await()
            val randomKey = db.reference.push().key!!
            val date = Date()
            val message = Message(messageId = randomKey, senderId = auth.uid, timeStamp = date.time, imageUrl = filePath.toString())
            val messageRoom = if (auth.uid!! > receiverId) auth.uid + receiverId else receiverId + auth.uid
            db.reference.child("chats").child(messageRoom).child("messages").child(randomKey).setValue(message)
            emit(DataState.Success())
        }catch (e: Exception){
            emit(DataState.Error(e))
        }

    }

    override suspend fun getReceiverStatus(uid: String): Flow<DataState<String>> = callbackFlow {
        try {
            val valueListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(DataState.Success(snapshot.getValue(String::class.java)))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(DataState.Success(null))
                }
            }
            db.reference.child("Presence").child(uid).addValueEventListener(valueListener)
            awaitClose { close() }
//            val status = db.reference.child("Presence").child(uid).addValueEventListener(valueListener)
//            val statusTxt = status.getValue(String::class.java)
//                .map { dataSnapshot -> dataSnapshot.getValue(String::class.java) }.first()
//            emit(DataState.Success(statusTxt))
        }catch (e: Exception){
            trySend(DataState.Error(e))
        }
    }

    override suspend fun deleteMessage(receiverId: String, message: Message): Flow<DataState<Boolean>> = flow {
        try {
            val messageRoom = if (auth.uid!! > receiverId) auth.uid + receiverId else receiverId + auth.uid
            db.reference.child("chats")
                .child(messageRoom)
                .child("messages")
                .child(message.messageId!!)
                .setValue(null).await()
            emit(DataState.Success())
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun sendMessage(messageTxt: String, receiverId: String): Flow<DataState<Boolean>> = flow {
        try {
            val date = Date()
            val randomKey = db.reference.push().key!!
            val messageRoom = if (auth.uid!! > receiverId) auth.uid + receiverId else receiverId + auth.uid
            val message = Message(messageId = randomKey, message = messageTxt, senderId = auth.uid, timeStamp = date.time)
            db.reference.child("chats").child(messageRoom).child("messages").child(randomKey).setValue(message)
            emit(DataState.Success())
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun textTyping(): Flow<DataState<Unit>> = flow {
        try {
            db.reference.child("Presence").child(auth.uid!!).setValue("typing...")
            val userStoppedTyping = Runnable { db.reference.child("Presence").child(auth.uid!!).setValue("Online") }
            val handler = Handler()
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(userStoppedTyping, 1000)
            emit(DataState.Success())
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun getUsersForChat(): Flow<DataState<List<User>>> = callbackFlow {
        try {
//            val users = db.reference.child("users").get().await().children
//                .map { dataSnapshot ->
//                    dataSnapshot.getValue(User::class.java)!! }
//                .filter { user ->
//                    user.uuid != auth.uid
//                }
//            emit(DataState.Success(data = users))
            val valueListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.children
                        .map { it.getValue(User::class.java)!! }
                        .filter { user -> user.uuid != auth.uid }
                    trySend(DataState.Success(users))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(DataState.Success(null))
                }
            }
            db.reference.child("users").addValueEventListener(valueListener)
            awaitClose { close() }
        }catch (e: Exception){
            trySend(DataState.Error(e))
        }
    }

    override suspend fun getMessages(receiverId: String): Flow<DataState<List<Message>>> = callbackFlow {
        try {
            val messageRoom = if (auth.uid!! > receiverId) auth.uid + receiverId else receiverId + auth.uid

            val valueListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(DataState.Success(snapshot.children.map { it.getValue(Message::class.java)!! }))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(DataState.Success(null))
                }
            }

            db.reference.child("chats").child(messageRoom).child("messages")
                .addValueEventListener(valueListener)

            awaitClose { close() }
        }catch (e: Exception){
            trySend(DataState.Error(e))
        }
    }
}