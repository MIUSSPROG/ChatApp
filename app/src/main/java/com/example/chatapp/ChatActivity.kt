package com.example.chatapp

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var users: ArrayList<User>
    private lateinit var usersAdapter: UserAdapter
    private lateinit var dialog: ProgressDialog
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = ProgressDialog(this)
        dialog.setMessage("Uploading Image...")
        dialog.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        users = ArrayList()
        usersAdapter = UserAdapter(this, users)

        binding.rv.adapter = usersAdapter
        database.reference.child("users").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (snapshot1 in snapshot.children){
                    val user = snapshot1.getValue(User::class.java)
                    if (user!!.uuid != FirebaseAuth.getInstance().uid){
                        users.add(user)
                    }
                }
                usersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("Presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("Presence")
            .child(currentId!!).setValue("Offline")
    }
}