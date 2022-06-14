package com.example.chatapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.bumptech.glide.Glide
import com.example.chatapp.adapter.MessagesAdapter
import com.example.chatapp.databinding.ActivityMessagingBinding
import com.example.chatapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessagingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagingBinding
    private lateinit var adapter: MessagesAdapter
    private var messages: ArrayList<Message>? = null
    private var messageRoom: String? = null
    private lateinit var database: FirebaseDatabase
    private var storage: FirebaseStorage? = null
    private lateinit var dialog: ProgressDialog
    private var senderUid: String? = null
    private var receiverUid: String? = null
    private val resultLauncher= registerForActivityResult(StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data!= null && it.data!!.data != null) {
            val uri = it.data!!.data!!
            val calendar = Calendar.getInstance()
            var reference = storage!!.reference.child("chats")
                .child(calendar.timeInMillis.toString())
            dialog.show()
            reference.putFile(uri)
                .addOnCompleteListener { task ->
                    dialog.dismiss()
                    if (task.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener {
                            val randomKey = database.reference.push().key
                            val filePath = it.toString()
                            val date = Date()
                            val message = Message(messageId = randomKey, senderId = senderUid, timeStamp = date.time, imageUrl = filePath)
                            binding.messageBox.setText("")

                            database.reference.child("chats")
                                .child(messageRoom!!)
                                .child("messages")
                                .child(randomKey!!)
                                .setValue(message)

                        }
                    }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessagingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this)
        dialog.setMessage("Uploading image")
        dialog.setCancelable(false)
        messages = ArrayList()
        val name = intent.getStringExtra("name")
        val profile = intent.getStringExtra("image")
        binding.toolbarProfileName.text = name
        Glide.with(this).load(profile)
            .placeholder(R.drawable.ic_image)
            .into(binding.toolbarProfileImage)
        binding.backBtn.setOnClickListener{ finish() }
        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().uid
        database.reference.child("Presence").child(receiverUid!!)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val status = snapshot.getValue(String::class.java)
                        if (status == "Offline"){
                            binding.toolbarProfileStatus.visibility = View.GONE
                        }else{
                            binding.toolbarProfileStatus.visibility = View.VISIBLE
                            binding.toolbarProfileStatus.text = status
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        if (senderUid!! > receiverUid!!){
            messageRoom = senderUid + receiverUid
        }
        else{
            messageRoom = receiverUid + senderUid
        }

        adapter = MessagesAdapter(this, messages!!, messageRoom!!)
        binding.rvMessages.adapter = adapter
        database.reference.child("chats")
            .child(messageRoom!!)
            .child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages!!.clear()
                    for (snapshot1 in snapshot.children){
                        val message = snapshot1.getValue(Message::class.java)
//                        message!!.messageId = snapshot1.key
                        messages!!.add(message!!)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        binding.btnSend.setOnClickListener {

            val messageTxt = binding.messageBox.text.toString()
            val date = Date()
            val randomKey = database.reference.push().key
            val message = Message(messageId = randomKey, message = messageTxt, senderId = senderUid, timeStamp = date.time)
            binding.messageBox.setText("")

            database.reference.child("chats").child(messageRoom!!)
                .child("messages")
                .child(randomKey!!)
                .setValue(message)
        }
        binding.btnAttachFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }

        val handler = Handler()
        binding.messageBox.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                database.reference.child("Presence")
                    .child(senderUid!!)
                    .setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }
            var userStoppedTyping = Runnable {
                database.reference.child("Presence")
                    .child(senderUid!!)
                    .setValue("Online")
            }

        })
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")
    }
}